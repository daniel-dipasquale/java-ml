package com.dipasquale.ai.rl.neat.speciation;

import com.dipasquale.ai.rl.neat.Context;
import com.dipasquale.ai.rl.neat.genotype.Genome;
import com.dipasquale.ai.rl.neat.phenotype.GenomeActivator;
import com.dipasquale.ai.rl.neat.speciation.organism.Organism;
import com.dipasquale.ai.rl.neat.speciation.organism.OrganismFactory;
import com.dipasquale.ai.rl.neat.speciation.strategy.fitness.FitnessEvaluationContext;
import com.dipasquale.ai.rl.neat.speciation.strategy.reproduction.ReproductionContext;
import com.dipasquale.ai.rl.neat.speciation.strategy.reproduction.SpeciesState;
import com.dipasquale.ai.rl.neat.speciation.strategy.selection.ChampionOrganismMissingException;
import com.dipasquale.ai.rl.neat.speciation.strategy.selection.SelectionContext;
import com.dipasquale.data.structure.deque.NodeDeque;
import com.dipasquale.data.structure.deque.StandardNode;
import com.dipasquale.data.structure.deque.StandardNodeDeque;
import com.dipasquale.data.structure.iterable.IterableSupport;
import com.dipasquale.data.structure.set.DequeIdentitySet;
import com.dipasquale.data.structure.set.DequeSet;
import com.dipasquale.io.serialization.SerializableStateGroup;
import lombok.AccessLevel;
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

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class Population {
    private static final Comparator<Species> SHARED_FITNESS_COMPARATOR = Comparator.comparing(Species::getSharedFitness);
    private final PopulationState populationState;
    private final DequeSet<Organism> organismsWithoutSpecies;
    private final Queue<OrganismFactory> organismsToBirth;
    private final NodeDeque<Species, StandardNode<Species>> speciesNodes;
    @Getter
    private final OrganismActivator championOrganismActivator;

    public Population() {
        this(new PopulationState(), new DequeIdentitySet<>(), new LinkedList<>(), new StandardNodeDeque<>(), new OrganismActivator());
    }

    public int getIteration() {
        return populationState.getIteration();
    }

    public int getGeneration() {
        return populationState.getGeneration();
    }

    private void fillOrganismsWithoutSpeciesWithGenesisGenomes(final Context context) {
        Context.SpeciationSupport speciationSupport = context.speciation();
        Context.NodeGeneSupport nodeGeneSupport = context.nodeGenes();

        for (int i = 0, c = context.general().params().populationSize(); i < c; i++) {
            Genome genome = speciationSupport.createGenesisGenome(context);
            Organism organism = new Organism(genome, populationState);

            organism.registerNodeGenes(nodeGeneSupport);
            organismsWithoutSpecies.add(organism);
        }
    }

    private int countOrganismsInAllSpecies() {
        return speciesNodes.flattenedStream()
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

    private StandardNode<Species> addSpecies(final Species species) {
        StandardNode<Species> speciesNode = speciesNodes.createUnbound(species);

        speciesNodes.add(speciesNode);

        return speciesNode;
    }

    private void assignOrganismsToSpecies(final Context context) {
        Iterable<Organism> organismsBirthed = organismsToBirth.stream()
                .map(organismFactory -> organismFactory.create(context))
                ::iterator;

        Context.SpeciationSupport speciationSupport = context.speciation();
        OrganismMatchMaker matchMaker = new OrganismMatchMaker(speciationSupport);
        int maximumSpecies = speciationSupport.params().maximumSpecies();

        for (Organism organism : IterableSupport.concatenate(organismsWithoutSpecies, organismsBirthed)) {
            for (StandardNode<Species> speciesNode : speciesNodes) {
                matchMaker.replaceIfBetterMatch(organism, speciesNodes.getValue(speciesNode));
            }

            if (speciesNodes.size() < maximumSpecies && !matchMaker.isBestMatchCompatible(populationState.getGeneration())) {
                addSpecies(createSpecies(speciationSupport, organism));
            } else {
                matchMaker.getBestMatch().add(organism);
            }

            matchMaker.clear();
        }

        organismsWithoutSpecies.clear();
        organismsToBirth.clear();
        context.metrics().collectAllSpeciesCompositions(speciesNodes::flattenedIterator);
    }

    public void initializeChampionOrganism(final Organism organism, final GenomeActivator genomeActivator) {
        championOrganismActivator.initialize(organism, genomeActivator);
    }

    private void initializeChampionOrganism(final Organism organism, final Context context) {
        initializeChampionOrganism(organism.createClone(context.connectionGenes()), organism.getActivator(context.activation()));
    }

    public void initialize(final Context context) {
        fillOrganismsWithoutSpeciesWithGenesisGenomes(context);
        initializeChampionOrganism(organismsWithoutSpecies.getFirst(), context);
        assignOrganismsToSpecies(context);
    }

    public void updateFitness(final Context context) {
        context.speciation().getFitnessEvaluationStrategy().calculate(new FitnessEvaluationContext(context, speciesNodes));
        context.metrics().prepareNextFitnessEvaluation();
    }

    private SpeciesState createSpeciesState(final Context.SpeciationSupport speciationSupport) {
        List<Species> all = new ArrayList<>();
        float[] totalSharedFitness = {0f};

        List<Species> ranked = speciesNodes.flattenedStream()
                .peek(all::add)
                .peek(species -> totalSharedFitness[0] += species.getSharedFitness())
                .filter(species -> !species.isStagnant(speciationSupport))
                .sorted(SHARED_FITNESS_COMPARATOR)
                .collect(Collectors.toList());

        if (!ranked.isEmpty()) {
            return new SpeciesState(all, ranked, totalSharedFitness[0]);
        }

        throw new PopulationExtinctionException("not enough organisms are allowed to reproduce the next generation");
    }

    private void reproduceThroughAllSpecies(final SelectionContext selectionContext) {
        Context context = selectionContext.getParent();
        Context.SpeciationSupport speciationSupport = context.speciation();
        SpeciesState speciesState = createSpeciesState(speciationSupport);
        ReproductionContext reproductionContext = new ReproductionContext(context, speciesState, organismsWithoutSpecies, organismsToBirth);

        selectionContext.getParent().speciation().getReproductionStrategy().reproduce(reproductionContext);
    }

    public void evolve(final Context context) {
        SelectionContext selectionContext = new SelectionContext(context, this);

        assert context.general().params().populationSize() == countOrganismsEverywhere();
        assert organismsWithoutSpecies.isEmpty();
        assert organismsToBirth.isEmpty() && context.speciation().getDisposedGenomeIdCount() == 0;

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
        context.connectionGenes().reset();
        context.nodeGenes().reset();
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

    public static Population create(final ObjectInputStream inputStream)
            throws IOException, ClassNotFoundException {
        SerializableStateGroup stateGroup = new SerializableStateGroup();

        stateGroup.readFrom(inputStream);

        PopulationState populationState = stateGroup.get("population.populationState");
        DequeSet<Organism> organismsWithoutSpecies = stateGroup.get("population.organismsWithoutSpecies");
        Queue<OrganismFactory> organismsToBirth = stateGroup.get("population.organismsToBirth");
        NodeDeque<Species, StandardNode<Species>> speciesNodes = stateGroup.get("population.speciesNodes");
        OrganismActivator championOrganismActivator = stateGroup.get("population.championOrganismActivator");

        return new Population(populationState, organismsWithoutSpecies, organismsToBirth, speciesNodes, championOrganismActivator);
    }
}