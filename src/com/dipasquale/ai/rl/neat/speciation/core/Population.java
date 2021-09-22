package com.dipasquale.ai.rl.neat.speciation.core;

import com.dipasquale.ai.rl.neat.context.Context;
import com.dipasquale.ai.rl.neat.speciation.organism.Organism;
import com.dipasquale.ai.rl.neat.speciation.organism.OrganismActivator;
import com.dipasquale.ai.rl.neat.speciation.organism.OrganismFactory;
import com.dipasquale.ai.rl.neat.speciation.strategy.fitness.SpeciesFitnessContext;
import com.dipasquale.ai.rl.neat.speciation.strategy.reproduction.SpeciesReproductionContext;
import com.dipasquale.ai.rl.neat.speciation.strategy.selection.ChampionOrganismMissingException;
import com.dipasquale.ai.rl.neat.speciation.strategy.selection.SpeciesSelectionContext;
import com.dipasquale.common.serialization.SerializableStateGroup;
import com.dipasquale.data.structure.deque.NodeDeque;
import com.dipasquale.data.structure.deque.SimpleNode;
import com.dipasquale.data.structure.deque.SimpleNodeDeque;
import com.dipasquale.data.structure.set.DequeIdentitySet;
import com.dipasquale.data.structure.set.DequeSet;
import com.google.common.collect.Iterables;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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

    public int getGeneration() {
        return populationState.getGeneration();
    }

    private void fillOrganismsWithoutSpeciesWithGenesisGenomes(final Context context) {
        IntStream.range(0, context.general().params().populationSize())
                .mapToObj(i -> context.speciation().createGenesisGenome(context))
                .map(g -> new Organism(g, populationState))
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

    public void initialize(final Context context, final OrganismActivator championOrganismActivator) {
        fillOrganismsWithoutSpeciesWithGenesisGenomes(context);
        championOrganismActivator.initialize(organismsWithoutSpecies.getFirst(), context.neuralNetwork());
    }

    private Species createSpecies(final Context.SpeciationSupport speciationSupport, final Organism organism) {
        return new Species(speciationSupport.createSpeciesId(), organism, populationState);
    }

    private SimpleNode<Species> addSpecies(final Species species) {
        SimpleNode<Species> speciesNode = speciesNodes.createUnbound(species);

        speciesNodes.add(speciesNode);

        return speciesNode;
    }

    private void assignOrganismsToSpecies(final Context context) {
        ClosestSpeciesCollector closestSpeciesCollector = new ClosestSpeciesCollector(context.speciation());

        Iterable<Organism> organismsBirthed = organismsToBirth.stream()
                .map(of -> of.create(context))
                ::iterator;

        for (Organism organism : Iterables.concat(organismsWithoutSpecies, organismsBirthed)) {
            for (SimpleNode<Species> speciesNode : speciesNodes) {
                closestSpeciesCollector.collectIfCloser(organism, speciesNodes.getValue(speciesNode));
            }

            if (!closestSpeciesCollector.isClosestCompatible(populationState.getGeneration())) {
                addSpecies(createSpecies(context.speciation(), organism));
            } else {
                closestSpeciesCollector.get().add(organism);
            }

            closestSpeciesCollector.clear();
        }

        organismsWithoutSpecies.clear();
        organismsToBirth.clear();
    }

    public int getSpeciesCount() {
        return speciesNodes.size();
    }

    public void updateFitness(final Context context) {
        assignOrganismsToSpecies(context);
        context.speciation().getFitnessStrategy().update(new SpeciesFitnessContext(context, speciesNodes));
    }

    private void reproduceThroughAllSpecies(final SpeciesSelectionContext selectionContext) {
        List<Species> rankedSpecies = speciesNodes.stream()
                .map(speciesNodes::getValue)
                .sorted(SHARED_FITNESS_COMPARATOR)
                .collect(Collectors.toList());

        SpeciesReproductionContext reproductionContext = new SpeciesReproductionContext(selectionContext, rankedSpecies, organismsWithoutSpecies, organismsToBirth);

        selectionContext.getParent().speciation().getReproductionStrategy().reproduce(reproductionContext);
    }

    public void evolve(final Context context, final OrganismActivator championOrganismActivator) {
        assert context.general().params().populationSize() == countOrganismsEverywhere();
        assert organismsWithoutSpecies.isEmpty();
        assert organismsToBirth.isEmpty() && context.speciation().getGenomeKilledCount() == 0;

        SpeciesSelectionContext selectionContext = new SpeciesSelectionContext(context, championOrganismActivator);

        try {
            context.speciation().getSelectionStrategy().select(selectionContext, speciesNodes);
        } catch (ChampionOrganismMissingException e) {
            throw new PopulationExtinctionException("the population has gone extinct", e);
        }

        assert organismsWithoutSpecies.isEmpty();
        assert organismsToBirth.isEmpty();

        reproduceThroughAllSpecies(selectionContext);
        populationState.increaseGeneration();

        assert context.general().params().populationSize() == countOrganismsEverywhere();
        assert context.speciation().getGenomeKilledCount() == organismsToBirth.size();
    }

    public void restart(final Context context, final OrganismActivator championOrganismActivator) {
        populationState.restart();
        context.connections().reset();
        context.nodes().reset();
        context.speciation().clearGenomeIds();
        organismsWithoutSpecies.clear();
        organismsToBirth.clear();
        context.speciation().clearSpeciesIds();
        speciesNodes.clear();
        initialize(context, championOrganismActivator);
    }

    public void save(final ObjectOutputStream outputStream)
            throws IOException {
        SerializableStateGroup state = new SerializableStateGroup();

        state.put("population.populationState", populationState);
        state.put("population.organismsWithoutSpecies", organismsWithoutSpecies);
        state.put("population.organismsToBirth", organismsToBirth);
        state.put("population.speciesNodes", speciesNodes);
        state.writeTo(outputStream);
    }

    public void load(final ObjectInputStream inputStream)
            throws IOException, ClassNotFoundException {
        SerializableStateGroup state = new SerializableStateGroup();

        state.readFrom(inputStream);
        populationState = state.get("population.populationState");
        organismsWithoutSpecies = state.get("population.organismsWithoutSpecies");
        organismsToBirth = state.get("population.organismsToBirth");
        speciesNodes = state.get("population.speciesNodes");
    }
}