package com.dipasquale.ai.rl.neat.speciation.core;

import com.dipasquale.ai.rl.neat.context.Context;
import com.dipasquale.ai.rl.neat.speciation.organism.Organism;
import com.dipasquale.ai.rl.neat.speciation.organism.OrganismActivator;
import com.dipasquale.ai.rl.neat.speciation.organism.OrganismFactory;
import com.dipasquale.ai.rl.neat.speciation.strategy.fitness.SpeciesFitnessContext;
import com.dipasquale.ai.rl.neat.speciation.strategy.reproduction.SpeciesReproductionContext;
import com.dipasquale.ai.rl.neat.speciation.strategy.selection.SpeciesSelectionContext;
import com.dipasquale.common.SerializableInteroperableStateMap;
import com.dipasquale.data.structure.deque.NodeDeque;
import com.dipasquale.data.structure.deque.SimpleNode;
import com.dipasquale.data.structure.deque.SimpleNodeDeque;
import com.dipasquale.data.structure.set.DequeIdentitySet;
import com.dipasquale.data.structure.set.DequeSet;
import com.google.common.collect.Iterables;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public final class Population {
    private static final Comparator<Species> SHARED_FITNESS_COMPARATOR = Comparator.comparing(Species::getSharedFitness);
    private PopulationState populationState;
    private DequeSet<Organism> organismsWithoutSpecies;
    private Queue<OrganismFactory> organismsToBirth;
    private NodeDeque<Species, SimpleNode<Species>> speciesNodes;
    private OrganismActivator championOrganismActivator;

    public Population(final OrganismActivator championOrganismActivator) {
        this.populationState = new PopulationState();
        this.organismsWithoutSpecies = new DequeIdentitySet<>();
        this.organismsToBirth = new LinkedList<>();
        this.speciesNodes = new SimpleNodeDeque<>();
        this.championOrganismActivator = championOrganismActivator;
    }

    public int getGeneration() {
        return populationState.getGeneration();
    }

    private boolean isInitialized() {
        return !(organismsWithoutSpecies.isEmpty() && organismsToBirth.isEmpty() && speciesNodes.isEmpty());
    }

    private void fillOrganismsWithoutSpeciesWithGenesisGenomes(final Context context) {
        IntStream.range(0, context.general().params().populationSize())
                .mapToObj(i -> populationState.getHistoricalMarkings().createGenome(context))
                .map(g -> new Organism(g, populationState))
                .peek(o -> o.initialize(context))
                .peek(Organism::freeze)
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

    public void initialize(final Context context) {
        if (isInitialized() && context.general().params().populationSize() != countOrganismsEverywhere()) {
            throw new IllegalStateException("unable to change the population size after initialization ... yet!");
        }

        populationState.getHistoricalMarkings().initialize(context);

        if (isInitialized()) {
            organismsWithoutSpecies.forEach(o -> o.initialize(context));

            speciesNodes.stream()
                    .map(speciesNodes::getValue)
                    .map(Species::getOrganisms)
                    .flatMap(List::stream)
                    .forEach(o -> o.initialize(context));
        } else {
            fillOrganismsWithoutSpeciesWithGenesisGenomes(context);
            championOrganismActivator.setOrganism(organismsWithoutSpecies.getFirst());
        }
    }

    private Species createSpecies(final Organism organism) {
        return new Species(organism, populationState);
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
                addSpecies(createSpecies(organism));
                closestSpeciesCollector.clear();
            } else {
                closestSpeciesCollector.getAndClear().add(organism);
            }
        }

        organismsWithoutSpecies.clear();
        organismsToBirth.clear();
    }

    public int getSpeciesCount() {
        return speciesNodes.size();
    }

    public void updateFitness(final Context context) {
        assignOrganismsToSpecies(context);
        context.speciation().fitnessStrategy().update(new SpeciesFitnessContext(context, speciesNodes));
    }

    private void reproduceThroughAllSpecies(final SpeciesSelectionContext selectionContext) {
        List<Species> rankedSpecies = speciesNodes.stream()
                .map(speciesNodes::getValue)
                .sorted(SHARED_FITNESS_COMPARATOR)
                .collect(Collectors.toList());

        SpeciesReproductionContext reproductionContext = new SpeciesReproductionContext(selectionContext, rankedSpecies, organismsWithoutSpecies, organismsToBirth);

        selectionContext.getParent().speciation().reproductionStrategy().reproduce(reproductionContext);
    }

    public void evolve(final Context context) {
        assert context.general().params().populationSize() == countOrganismsEverywhere();
        assert organismsWithoutSpecies.isEmpty();
        assert organismsToBirth.isEmpty() && populationState.getHistoricalMarkings().getGenomeKilledCount() == 0;

        SpeciesSelectionContext selectionContext = new SpeciesSelectionContext(context, championOrganismActivator);

        context.speciation().selectionStrategy().select(selectionContext, speciesNodes);

        assert organismsWithoutSpecies.isEmpty();
        assert organismsToBirth.isEmpty();

        reproduceThroughAllSpecies(selectionContext);
        populationState.increaseGeneration();

        assert context.general().params().populationSize() == countOrganismsEverywhere();
        assert populationState.getHistoricalMarkings().getGenomeKilledCount() == organismsToBirth.size();
    }

    public void restart(final Context context) {
        populationState.restartGeneration();
        populationState.getHistoricalMarkings().reset(context.nodes());
        organismsWithoutSpecies.clear();
        fillOrganismsWithoutSpeciesWithGenesisGenomes(context);
        championOrganismActivator.setOrganism(organismsWithoutSpecies.getFirst());
        organismsToBirth.clear();
        speciesNodes.clear();
    }

    public void save(final ObjectOutputStream outputStream)
            throws IOException {
        SerializableInteroperableStateMap state = new SerializableInteroperableStateMap();

        state.put("population.populationState", populationState);
        state.put("population.organismsWithoutSpecies", organismsWithoutSpecies);
        state.put("population.organismsToBirth", organismsToBirth);
        state.put("population.speciesNodes", speciesNodes);
        state.writeTo(outputStream);
    }

    public void load(final ObjectInputStream inputStream, final OrganismActivator championOrganismActivatorOverride)
            throws IOException, ClassNotFoundException {
        SerializableInteroperableStateMap state = new SerializableInteroperableStateMap();

        state.readFrom(inputStream);
        populationState = state.get("population.populationState");
        organismsWithoutSpecies = state.get("population.organismsWithoutSpecies");
        organismsToBirth = state.get("population.organismsToBirth");
        speciesNodes = state.get("population.speciesNodes");
        championOrganismActivator = championOrganismActivatorOverride;
    }
}