package com.dipasquale.ai.rl.neat.speciation;

import com.dipasquale.ai.rl.neat.context.Context;
import com.dipasquale.data.structure.deque.NodeDeque;
import com.dipasquale.data.structure.deque.SimpleNode;
import com.dipasquale.data.structure.deque.SimpleNodeDeque;
import com.dipasquale.data.structure.map.SerializableInteroperableStateMap;
import com.dipasquale.data.structure.set.DequeSet;
import com.dipasquale.data.structure.set.IdentityDequeSet;
import com.google.common.collect.Iterables;
import lombok.Getter;

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
    @Getter
    private int generation;
    @Getter
    private PopulationHistoricalMarkings historicalMarkings;
    private DequeSet<Organism> organismsWithoutSpecies;
    private Queue<OrganismFactory> organismsToBirth;
    private NodeDeque<Species, SimpleNode<Species>> speciesNodes;
    private OrganismActivator mostFitOrganismActivator;
    private List<SpeciesFitnessStrategy> speciesFitnessStrategies;
    private List<SpeciesEvolutionStrategy> speciesEvolutionStrategies;
    private SpeciesBreedContext speciesBreedContext;
    private List<SpeciesBreedStrategy> speciesBreedStrategies;

    public Population(final OrganismActivator mostFitOrganismActivator) {
        this.generation = 1;
        this.historicalMarkings = new PopulationHistoricalMarkings();
        this.organismsWithoutSpecies = new IdentityDequeSet<>();
        this.organismsToBirth = new LinkedList<>();
        this.speciesNodes = new SimpleNodeDeque<>();
        this.mostFitOrganismActivator = mostFitOrganismActivator;
        this.speciesFitnessStrategies = new LinkedList<>();
        this.speciesEvolutionStrategies = new LinkedList<>();
        this.speciesBreedContext = null;
        this.speciesBreedStrategies = new LinkedList<>();
    }

    private boolean isInitialized() {
        return !(organismsWithoutSpecies.isEmpty() && organismsToBirth.isEmpty() && speciesNodes.isEmpty());
    }

    private void fillOrganismsWithoutSpeciesWithGenesisGenomes(final Context context) {
        IntStream.range(0, context.general().populationSize())
                .mapToObj(i -> historicalMarkings.createGenome(context))
                .map(g -> new Organism(g, this))
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
            speciesFitnessStrategies.add(new SpeciesFitnessStrategyUpdateOrganisms(context));
            speciesFitnessStrategies.add(new SpeciesFitnessStrategyUpdateSpecies());
        } else {
            speciesFitnessStrategies.add(new SpeciesFitnessStrategyDefault(context.general()));
        }
    }

    private void replaceSpeciesEvolutionStrategies(final Context context) {
        speciesEvolutionStrategies.clear();
        speciesEvolutionStrategies.add(new SpeciesEvolutionStrategyRemoveLeastFit(context));
        speciesEvolutionStrategies.add(new SpeciesEvolutionStrategyTotalSharedFitness());
        speciesEvolutionStrategies.add(new SpeciesEvolutionStrategySelectMostElites(context.speciation(), organismsWithoutSpecies));
        speciesEvolutionStrategies.add(new SpeciesEvolutionStrategySelectMostElite(organismsWithoutSpecies, mostFitOrganismActivator));
    }

    private void replaceSpeciesBreedStrategies(final Context context) {
        speciesBreedStrategies.clear();
        speciesBreedStrategies.add(new SpeciesBreedStrategyInterSpecies(context, organismsWithoutSpecies, organismsToBirth));
        speciesBreedStrategies.add(new SpeciesBreedStrategyWithinSpecies(context, organismsWithoutSpecies, organismsToBirth));
        speciesBreedStrategies.add(new SpeciesBreedStrategyGenesis(context, organismsWithoutSpecies, organismsToBirth));
    }

    public void initialize(final Context context) {
        if (isInitialized() && context.general().populationSize() != countOrganismsEverywhere()) {
            throw new IllegalStateException("unable to change the population size after initialization ... yet!");
        }

        historicalMarkings.initialize(context);

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
        return new Species(this, organism);
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
                } else {
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
        if (speciesBreedContext == null) {
            speciesBreedContext = new SpeciesBreedContext(evolutionContext);
        } else {
            speciesBreedContext = new SpeciesBreedContext(evolutionContext, speciesBreedContext.getInterSpeciesBreedingLeftOverRatio());
        }

        List<Species> speciesList = speciesNodes.stream()
                .map(speciesNodes::getValue)
                .sorted(SHARED_FITNESS_COMPARATOR)
                .collect(Collectors.toList());

        for (SpeciesBreedStrategy speciesBreedStrategy : speciesBreedStrategies) {
            speciesBreedStrategy.process(speciesBreedContext, speciesList);
        }
    }

    public void evolve(final Context context) {
        SpeciesEvolutionContext evolutionContext = new SpeciesEvolutionContext();

        assert context.general().populationSize() == countOrganismsEverywhere();
        assert organismsToBirth.isEmpty() && historicalMarkings.getGenomeKilledCount() == 0;

        prepareAllSpeciesForEvolution(context, evolutionContext);

        assert organismsToBirth.isEmpty();

        breedThroughAllSpecies(evolutionContext);
        generation++;

        assert context.general().populationSize() == countOrganismsEverywhere();
        assert historicalMarkings.getGenomeKilledCount() == organismsToBirth.size();
    }

    public void restart(final Context context) {
        generation = 1;
        historicalMarkings.reset(context.nodes());
        organismsWithoutSpecies.clear();
        fillOrganismsWithoutSpeciesWithGenesisGenomes(context);
        mostFitOrganismActivator.setOrganism(organismsWithoutSpecies.getFirst());
        organismsToBirth.clear();
        speciesNodes.clear();
        speciesBreedContext = null;
    }

    public void save(final ObjectOutputStream outputStream)
            throws IOException {
        SerializableInteroperableStateMap state = new SerializableInteroperableStateMap();

        state.put("population.generation", generation);
        state.put("population.historicalMarkings", historicalMarkings);
        state.put("population.organismsWithoutSpecies", organismsWithoutSpecies);
        state.put("population.organismsToBirth", organismsToBirth);
        state.put("population.speciesNodes", speciesNodes);
        state.put("population.speciesBreedContext", speciesBreedContext);
        state.writeTo(outputStream);
    }

    public void load(final ObjectInputStream inputStream, final OrganismActivator mostFitOrganismActivatorOverride)
            throws IOException, ClassNotFoundException {
        SerializableInteroperableStateMap state = new SerializableInteroperableStateMap();

        state.readFrom(inputStream);
        generation = state.get("population.generation");
        historicalMarkings = state.get("population.historicalMarkings");
        organismsWithoutSpecies = state.get("population.organismsWithoutSpecies");
        organismsToBirth = state.get("population.organismsToBirth");
        speciesNodes = state.get("population.speciesNodes");
        mostFitOrganismActivator = mostFitOrganismActivatorOverride;
        speciesFitnessStrategies = new LinkedList<>();
        speciesEvolutionStrategies = new LinkedList<>();
        speciesBreedContext = state.get("population.speciesBreedContext");
        speciesBreedStrategies = new LinkedList<>();
    }
}