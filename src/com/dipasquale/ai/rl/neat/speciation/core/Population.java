package com.dipasquale.ai.rl.neat.speciation.core;

import com.dipasquale.ai.rl.neat.context.Context;
import com.dipasquale.ai.rl.neat.phenotype.GenomeActivator;
import com.dipasquale.ai.rl.neat.speciation.organism.Organism;
import com.dipasquale.ai.rl.neat.speciation.organism.OrganismFactory;
import com.dipasquale.ai.rl.neat.speciation.strategy.fitness.SpeciesFitnessContext;
import com.dipasquale.ai.rl.neat.speciation.strategy.reproduction.SpeciesReproductionContext;
import com.dipasquale.ai.rl.neat.speciation.strategy.reproduction.SpeciesState;
import com.dipasquale.ai.rl.neat.speciation.strategy.selection.ChampionOrganismMissingException;
import com.dipasquale.ai.rl.neat.speciation.strategy.selection.SpeciesSelectionContext;
import com.dipasquale.common.serialization.SerializableStateGroup;
import com.dipasquale.data.structure.deque.NodeDeque;
import com.dipasquale.data.structure.deque.SimpleNode;
import com.dipasquale.data.structure.deque.SimpleNodeDeque;
import com.dipasquale.data.structure.set.DequeIdentitySet;
import com.dipasquale.data.structure.set.DequeSet;
import com.google.common.collect.Iterables;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@RequiredArgsConstructor
public final class Population {
    private static final Comparator<Species> SHARED_FITNESS_COMPARATOR = Comparator.comparing(Species::getSharedFitness);
    private PopulationState populationState = new PopulationState();
    private DequeSet<Organism> organismsWithoutSpecies = new DequeIdentitySet<>();
    private Queue<OrganismFactory> organismsToBirth = new LinkedList<>();
    private NodeDeque<Species, SimpleNode<Species>> speciesNodes = new SimpleNodeDeque<>();
    @Getter
    private OrganismActivator championOrganismActivator = new OrganismActivator();

    public int getIteration() {
        return populationState.getIteration();
    }

    public int getGeneration() {
        return populationState.getGeneration();
    }

    private void fillOrganismsWithoutSpeciesWithGenesisGenomes(final Context context) {
        IntStream.range(0, context.general().params().populationSize())
                .mapToObj(i -> context.speciation().createGenesisGenome(context))
                .map(g -> new Organism(g, populationState))
                .peek(o -> o.registerNodes(context.connections()))
                .forEach(organismsWithoutSpecies::add);
    }

    private int countOrganismsInAllSpecies() {
        return speciesNodes.stream()
                .map(speciesNodes::getValue)
                .map(Species::getOrganisms)
                .map(List::size)
                .reduce(0, Integer::sum);
    }

    private int countOrganismsEverywhere() {
        return organismsWithoutSpecies.size() + organismsToBirth.size() + countOrganismsInAllSpecies();
    }

    public int getSpeciesCount() {
        return speciesNodes.size();
    }

    private Species createSpecies(final Context.SpeciationSupport speciationSupport, final Organism organism) {
        return new Species(speciationSupport.createSpeciesId(), organism, populationState);
    }

    private SimpleNode<Species> addSpecies(final Species species) {
        SimpleNode<Species> speciesNode = speciesNodes.createUnbound(species);

        speciesNodes.add(speciesNode);

        return speciesNode;
    }

    private void addCompositionMetrics(final Context.MetricSupport metricSupport) {
        Iterable<Species> allSpecies = speciesNodes.stream()
                .map(speciesNodes::getValue)
                ::iterator;

        metricSupport.collectCompositions(allSpecies);
    }

    private void assignOrganismsToSpecies(final Context context) {
        OrganismMatchMaker matchMaker = new OrganismMatchMaker(context.speciation());
        int maximumSpecies = context.speciation().params().maximumSpecies();

        Iterable<Organism> organismsBirthed = organismsToBirth.stream()
                .map(of -> of.create(context))
                ::iterator;

        for (Organism organism : Iterables.concat(organismsWithoutSpecies, organismsBirthed)) {
            for (SimpleNode<Species> speciesNode : speciesNodes) {
                matchMaker.collectIfBetterMatch(organism, speciesNodes.getValue(speciesNode));
            }

            if (speciesNodes.size() < maximumSpecies && !matchMaker.isBestMatchCompatible(populationState.getGeneration())) {
                addSpecies(createSpecies(context.speciation(), organism));
            } else {
                matchMaker.getBestMatch().add(organism);
            }

            matchMaker.clear();
        }

        organismsWithoutSpecies.clear();
        organismsToBirth.clear();
        addCompositionMetrics(context.metrics());
    }

    public void initializeChampionOrganism(final Organism organism, final GenomeActivator genomeActivator) {
        championOrganismActivator.initialize(organism, genomeActivator);
    }

    public void initialize(final Context context) {
        fillOrganismsWithoutSpeciesWithGenesisGenomes(context);
        initializeChampionOrganism(organismsWithoutSpecies.getFirst().createClone(context.connections()), organismsWithoutSpecies.getFirst().getActivator(context.activation()));
        assignOrganismsToSpecies(context);
    }

    public void updateFitness(final Context context) {
        context.speciation().getFitnessStrategy().update(new SpeciesFitnessContext(context, speciesNodes));
        context.metrics().prepareNextFitnessCalculation();
    }

    private SpeciesState createSpeciesState(final Context.SpeciationSupport speciationSupport) {
        List<Species> all = new ArrayList<>();
        float[] totalSharedFitness = new float[]{0f};

        List<Species> ranked = speciesNodes.stream()
                .map(speciesNodes::getValue)
                .peek(all::add)
                .peek(s -> totalSharedFitness[0] += s.getSharedFitness())
                .filter(s -> !s.isStagnant(speciationSupport))
                .sorted(SHARED_FITNESS_COMPARATOR)
                .collect(Collectors.toList());

        if (!ranked.isEmpty()) {
            return new SpeciesState(all, ranked, totalSharedFitness[0]);
        }

        throw new PopulationExtinctionException("not enough organisms are allowed to reproduce the next generation");
    }

    private void reproduceThroughAllSpecies(final SpeciesSelectionContext selectionContext) {
        Context context = selectionContext.getParent();
        Context.SpeciationSupport speciationSupport = context.speciation();
        SpeciesState speciesState = createSpeciesState(speciationSupport);
        SpeciesReproductionContext reproductionContext = new SpeciesReproductionContext(context, speciesState, organismsWithoutSpecies, organismsToBirth);

        selectionContext.getParent().speciation().getReproductionStrategy().reproduce(reproductionContext);
    }

    public void evolve(final Context context) {
        assert context.general().params().populationSize() == countOrganismsEverywhere();
        assert organismsWithoutSpecies.isEmpty();
        assert organismsToBirth.isEmpty() && context.speciation().getDisposedGenomeIdCount() == 0;

        SpeciesSelectionContext selectionContext = new SpeciesSelectionContext(context, this);

        try {
            context.speciation().getSelectionStrategy().select(selectionContext, speciesNodes);
        } catch (ChampionOrganismMissingException e) {
            throw new PopulationExtinctionException("the population has gone extinct", e);
        }

        assert organismsWithoutSpecies.isEmpty();
        assert organismsToBirth.isEmpty();

        reproduceThroughAllSpecies(selectionContext);
        populationState.increaseGeneration();
        context.metrics().prepareNextGeneration();

        assert context.general().params().populationSize() == countOrganismsEverywhere();
        assert context.speciation().getDisposedGenomeIdCount() == organismsToBirth.size();

        assignOrganismsToSpecies(context);

        assert context.general().params().populationSize() == countOrganismsEverywhere();
        assert organismsWithoutSpecies.isEmpty();
        assert organismsToBirth.isEmpty() && context.speciation().getDisposedGenomeIdCount() == 0;
    }

    public void restart(final Context context) {
        populationState.restart();
        context.connections().reset();
        context.nodes().reset();
        context.speciation().clearGenomeIds();
        organismsWithoutSpecies.clear();
        organismsToBirth.clear();
        context.speciation().clearSpeciesIds();
        speciesNodes.clear();
        context.metrics().prepareNextIteration();
        championOrganismActivator.reset();
        initialize(context);
    }

    public void save(final ObjectOutputStream outputStream)
            throws IOException {
        SerializableStateGroup stateGroup = new SerializableStateGroup();

        stateGroup.put("population.populationState", populationState);
        stateGroup.put("population.organismsWithoutSpecies", organismsWithoutSpecies);
        stateGroup.put("population.organismsToBirth", organismsToBirth);
        stateGroup.put("population.speciesNodes", speciesNodes);
        stateGroup.put("population.championOrganismActivator", championOrganismActivator);
        stateGroup.writeTo(outputStream);
    }

    public void load(final ObjectInputStream inputStream)
            throws IOException, ClassNotFoundException {
        SerializableStateGroup stateGroup = new SerializableStateGroup();

        stateGroup.readFrom(inputStream);
        populationState = stateGroup.get("population.populationState");
        organismsWithoutSpecies = stateGroup.get("population.organismsWithoutSpecies");
        organismsToBirth = stateGroup.get("population.organismsToBirth");
        speciesNodes = stateGroup.get("population.speciesNodes");
        championOrganismActivator = stateGroup.get("population.championOrganismActivator");
    }
}