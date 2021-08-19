package com.dipasquale.ai.rl.neat.speciation.core;

import com.dipasquale.ai.rl.neat.context.Context;
import com.dipasquale.ai.rl.neat.speciation.organism.Organism;
import com.dipasquale.ai.rl.neat.speciation.organism.OrganismActivator;
import com.dipasquale.ai.rl.neat.speciation.organism.OrganismFactory;
import com.dipasquale.ai.rl.neat.speciation.strategy.breeding.GenesisSpeciesBreedingStrategy;
import com.dipasquale.ai.rl.neat.speciation.strategy.breeding.InterSpeciesBreedingStrategy;
import com.dipasquale.ai.rl.neat.speciation.strategy.breeding.MatchingSpeciesBreedingStrategy;
import com.dipasquale.ai.rl.neat.speciation.strategy.breeding.SpeciesBreedingContext;
import com.dipasquale.ai.rl.neat.speciation.strategy.breeding.SpeciesBreedingStrategy;
import com.dipasquale.ai.rl.neat.speciation.strategy.evolution.RemoveLeastFitSpeciesEvolutionStrategy;
import com.dipasquale.ai.rl.neat.speciation.strategy.evolution.SelectMostEliteSpeciesEvolutionStrategy;
import com.dipasquale.ai.rl.neat.speciation.strategy.evolution.SelectMostElitesSpeciesEvolutionStrategy;
import com.dipasquale.ai.rl.neat.speciation.strategy.evolution.SpeciesEvolutionContext;
import com.dipasquale.ai.rl.neat.speciation.strategy.evolution.SpeciesEvolutionStrategy;
import com.dipasquale.ai.rl.neat.speciation.strategy.evolution.TotalSharedFitnessSpeciesEvolutionStrategy;
import com.dipasquale.ai.rl.neat.speciation.strategy.fitness.DefaultSpeciesFitnessStrategy;
import com.dipasquale.ai.rl.neat.speciation.strategy.fitness.ParallelUpdateSpeciesFitnessStrategy;
import com.dipasquale.ai.rl.neat.speciation.strategy.fitness.SpeciesFitnessStrategy;
import com.dipasquale.ai.rl.neat.speciation.strategy.fitness.UpdateSharedSpeciesFitnessStrategy;
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
    private OrganismActivator mostFitOrganismActivator;
    private List<SpeciesFitnessStrategy> speciesFitnessStrategies;
    private List<SpeciesEvolutionStrategy> speciesEvolutionStrategies;
    private SpeciesBreedingContext speciesBreedingContext;
    private List<SpeciesBreedingStrategy> speciesBreedingStrategies;

    public Population(final OrganismActivator mostFitOrganismActivator) {
        this.populationState = new PopulationState();
        this.organismsWithoutSpecies = new DequeIdentitySet<>();
        this.organismsToBirth = new LinkedList<>();
        this.speciesNodes = new SimpleNodeDeque<>();
        this.mostFitOrganismActivator = mostFitOrganismActivator;
        this.speciesFitnessStrategies = new LinkedList<>();
        this.speciesEvolutionStrategies = new LinkedList<>();
        this.speciesBreedingContext = null;
        this.speciesBreedingStrategies = new LinkedList<>();
    }

    public int getGeneration() {
        return populationState.getGeneration();
    }

    private boolean isInitialized() {
        return !(organismsWithoutSpecies.isEmpty() && organismsToBirth.isEmpty() && speciesNodes.isEmpty());
    }

    private void fillOrganismsWithoutSpeciesWithGenesisGenomes(final Context context) {
        IntStream.range(0, context.general().populationSize())
                .mapToObj(i -> populationState.getHistoricalMarkings().createGenome(context))
                .map(g -> new Organism(g, populationState))
                .peek(o -> o.initialize(context))
                .peek(Organism::freeze)
                .forEach(organismsWithoutSpecies::add);
    }

    private int countOrganismsInSpecies() {
        return speciesNodes.stream()
                .map(speciesNodes::getValue)
                .map(Species::getOrganisms)
                .map(List::size)
                .reduce(0, Integer::sum);
    }

    private int countOrganismsEverywhere() {
        return organismsWithoutSpecies.size() + organismsToBirth.size() + countOrganismsInSpecies();
    }

    private void replaceSpeciesFitnessStrategies(final Context context) {
        speciesFitnessStrategies.clear();

        if (context.parallelism().isEnabled()) {
            speciesFitnessStrategies.add(new ParallelUpdateSpeciesFitnessStrategy(context));
            speciesFitnessStrategies.add(new UpdateSharedSpeciesFitnessStrategy());
        } else {
            speciesFitnessStrategies.add(new DefaultSpeciesFitnessStrategy(context.general()));
        }
    }

    private void replaceSpeciesEvolutionStrategies(final Context context) {
        speciesEvolutionStrategies.clear();
        speciesEvolutionStrategies.add(new RemoveLeastFitSpeciesEvolutionStrategy(context));
        speciesEvolutionStrategies.add(new TotalSharedFitnessSpeciesEvolutionStrategy());
        speciesEvolutionStrategies.add(new SelectMostElitesSpeciesEvolutionStrategy(context.speciation(), organismsWithoutSpecies));
        speciesEvolutionStrategies.add(new SelectMostEliteSpeciesEvolutionStrategy(organismsWithoutSpecies, mostFitOrganismActivator));
    }

    private void replaceSpeciesBreedStrategies(final Context context) {
        speciesBreedingStrategies.clear();
        speciesBreedingStrategies.add(new InterSpeciesBreedingStrategy(context, organismsWithoutSpecies, organismsToBirth));
        speciesBreedingStrategies.add(new MatchingSpeciesBreedingStrategy(context, organismsWithoutSpecies, organismsToBirth));
        speciesBreedingStrategies.add(new GenesisSpeciesBreedingStrategy(context, organismsWithoutSpecies, organismsToBirth));
    }

    public void initialize(final Context context) {
        if (isInitialized() && context.general().populationSize() != countOrganismsEverywhere()) {
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
            mostFitOrganismActivator.setOrganism(organismsWithoutSpecies.getFirst());
        }

        replaceSpeciesFitnessStrategies(context);
        replaceSpeciesEvolutionStrategies(context);
        replaceSpeciesBreedStrategies(context);
    }

    private boolean addOrganismToFirstCompatibleSpecies(final Context context, final Organism organism) {
        for (SimpleNode<Species> speciesNode : speciesNodes) {
            if (speciesNodes.getValue(speciesNode).addIfCompatible(context.speciation(), organism)) {
                return true;
            }
        }

        return false;
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
        Iterable<Organism> organismsNew = organismsToBirth.stream()
                .map(of -> of.create(context))
                ::iterator;

        for (Organism organism : Iterables.concat(organismsWithoutSpecies, organismsNew)) {
            if (!addOrganismToFirstCompatibleSpecies(context, organism)) {
                if (speciesNodes.size() < context.speciation().maximumSpecies()) {
                    addSpecies(createSpecies(organism));
                } else { // TODO: I feel like the statement below is a hack and I cannot think of a better fix, but the problem is that at higher generations, genomes fall so far apart from the distance metric that they all end up in singular species (FIX whenever I can think of a better solution)
                    organism.getMostCompatibleSpecies().add(context.speciation(), organism);
                }
            }
        }

        organismsWithoutSpecies.clear();
        organismsToBirth.clear();
    }

    private void updateFitnessInAllSpecies() {
        for (SpeciesFitnessStrategy speciesFitnessStrategy : speciesFitnessStrategies) {
            speciesFitnessStrategy.process(speciesNodes);
        }
    }

    public int getSpeciesCount() {
        return speciesNodes.size();
    }

    public void updateFitness(final Context context) {
        assignOrganismsToSpecies(context);
        updateFitnessInAllSpecies();
    }

    private void prepareAllSpeciesForEvolution(final Context context, final SpeciesEvolutionContext evolutionContext) {
        for (SimpleNode<Species> speciesNode = speciesNodes.peekFirst(); speciesNode != null; ) {
            Species species = speciesNodes.getValue(speciesNode);
            boolean shouldSurvive = speciesNodes.size() <= 2 || species.shouldSurvive(context.speciation());

            for (SpeciesEvolutionStrategy speciesEvolutionStrategy : speciesEvolutionStrategies) {
                speciesEvolutionStrategy.process(evolutionContext, species, shouldSurvive);
            }

            if (!shouldSurvive) {
                SimpleNode<Species> speciesNodeNext = speciesNodes.peekNext(speciesNode);

                speciesNodes.remove(speciesNode);
                speciesNode = speciesNodeNext;
            } else {
                speciesNode = speciesNodes.peekNext(speciesNode);
            }
        }

        for (SpeciesEvolutionStrategy speciesEvolutionStrategy : speciesEvolutionStrategies) {
            speciesEvolutionStrategy.postProcess(evolutionContext);
        }
    }

    private void breedThroughAllSpecies(final SpeciesEvolutionContext evolutionContext) {
        if (speciesBreedingContext == null) {
            speciesBreedingContext = new SpeciesBreedingContext(evolutionContext);
        } else {
            speciesBreedingContext = new SpeciesBreedingContext(evolutionContext, speciesBreedingContext.getInterSpeciesBreedingLeftOverRatio());
        }

        List<Species> speciesList = speciesNodes.stream()
                .map(speciesNodes::getValue)
                .sorted(SHARED_FITNESS_COMPARATOR)
                .collect(Collectors.toList());

        for (SpeciesBreedingStrategy speciesBreedingStrategy : speciesBreedingStrategies) {
            speciesBreedingStrategy.process(speciesBreedingContext, speciesList);
        }
    }

    public void evolve(final Context context) {
        SpeciesEvolutionContext evolutionContext = new SpeciesEvolutionContext();

        if (context.general().populationSize() != countOrganismsEverywhere()) {
            assert context.general().populationSize() == countOrganismsEverywhere();
        }

        assert context.general().populationSize() == countOrganismsEverywhere();
        assert organismsToBirth.isEmpty() && populationState.getHistoricalMarkings().getGenomeKilledCount() == 0;

        prepareAllSpeciesForEvolution(context, evolutionContext);

        assert organismsToBirth.isEmpty();

        breedThroughAllSpecies(evolutionContext);
        populationState.increaseGeneration();

        if (context.general().populationSize() != countOrganismsEverywhere()) {
            assert context.general().populationSize() == countOrganismsEverywhere();
        }

        assert context.general().populationSize() == countOrganismsEverywhere();
        assert populationState.getHistoricalMarkings().getGenomeKilledCount() == organismsToBirth.size();
    }

    public void restart(final Context context) {
        populationState.restartGeneration();
        populationState.getHistoricalMarkings().reset(context.nodes());
        organismsWithoutSpecies.clear();
        fillOrganismsWithoutSpeciesWithGenesisGenomes(context);
        mostFitOrganismActivator.setOrganism(organismsWithoutSpecies.getFirst());
        organismsToBirth.clear();
        speciesNodes.clear();
        speciesBreedingContext = null;
    }

    public void save(final ObjectOutputStream outputStream)
            throws IOException {
        SerializableInteroperableStateMap state = new SerializableInteroperableStateMap();

        state.put("population.populationState", populationState);
        state.put("population.organismsWithoutSpecies", organismsWithoutSpecies);
        state.put("population.organismsToBirth", organismsToBirth);
        state.put("population.speciesNodes", speciesNodes);
        state.put("population.speciesBreedingContext", speciesBreedingContext);
        state.writeTo(outputStream);
    }

    public void load(final ObjectInputStream inputStream, final OrganismActivator mostFitOrganismActivatorOverride)
            throws IOException, ClassNotFoundException {
        SerializableInteroperableStateMap state = new SerializableInteroperableStateMap();

        state.readFrom(inputStream);
        populationState = state.get("population.populationState");
        organismsWithoutSpecies = state.get("population.organismsWithoutSpecies");
        organismsToBirth = state.get("population.organismsToBirth");
        speciesNodes = state.get("population.speciesNodes");
        mostFitOrganismActivator = mostFitOrganismActivatorOverride;
        speciesFitnessStrategies = new LinkedList<>();
        speciesEvolutionStrategies = new LinkedList<>();
        speciesBreedingContext = state.get("population.speciesBreedingContext");
        speciesBreedingStrategies = new LinkedList<>();
    }
}