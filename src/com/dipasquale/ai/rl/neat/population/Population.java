package com.dipasquale.ai.rl.neat.population;

import com.dipasquale.ai.rl.neat.context.Context;
import com.dipasquale.ai.rl.neat.genotype.Organism;
import com.dipasquale.ai.rl.neat.genotype.OrganismFactory;
import com.dipasquale.ai.rl.neat.genotype.Species;
import com.dipasquale.data.structure.deque.NodeDeque;
import com.dipasquale.data.structure.deque.SimpleNode;
import com.dipasquale.data.structure.deque.SimpleNodeDeque;
import com.dipasquale.data.structure.set.DequeSet;
import com.dipasquale.data.structure.set.IdentityDequeSet;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public final class Population implements Serializable {
    private static final Comparator<Species> SHARED_FITNESS_COMPARATOR = Comparator.comparing(Species::getSharedFitness);
    @Serial
    private static final long serialVersionUID = 3565613163339474186L;
    private final Context context;
    private final DequeSet<Organism> organismsWithoutSpecies;
    private final Queue<OrganismFactory> organismsToBirth;
    private final NodeDeque<Species, SimpleNode<Species>> speciesNodes;
    private final OrganismActivator mostFitOrganismActivator;
    private SpeciesBreedContext speciesBreedContext;
    private final List<SpeciesFitnessStrategy> speciesFitnessStrategies;
    private final List<SpeciesEvolutionStrategy> speciesEvolutionStrategies;
    private final List<SpeciesBreedStrategy> speciesBreedStrategies;
    @Getter
    private int generation;

    public Population(final Context context, final OrganismActivator mostFitOrganismActivator) {
        DequeSet<Organism> organismsWithoutSpecies = createGenesisOrganisms(context, this);
        Queue<OrganismFactory> organismsToBirth = new LinkedList<>();

        this.context = context;
        this.organismsWithoutSpecies = organismsWithoutSpecies;
        this.organismsToBirth = organismsToBirth;
        this.speciesNodes = new SimpleNodeDeque<>();
        this.mostFitOrganismActivator = mostFitOrganismActivator;
        mostFitOrganismActivator.setOrganism(organismsWithoutSpecies.getFirst());
        this.generation = 1;
        this.speciesFitnessStrategies = createSpeciesFitnessStrategies(context);
        this.speciesEvolutionStrategies = createSpeciesEvolutionStrategies(context, organismsWithoutSpecies, mostFitOrganismActivator);
        this.speciesBreedStrategies = createSpeciesBreedStrategies(context, organismsWithoutSpecies, organismsToBirth);
    }

    private static void fillWithGenesisOrganisms(final Set<Organism> genesisOrganisms, final Context context, final Population population) {
        IntStream.range(0, context.general().populationSize())
                .mapToObj(i -> context.general().createGenesisGenome(context))
                .map(g -> new Organism(g, population, context.general()))
                .forEach(genesisOrganisms::add);
    }

    private static DequeSet<Organism> createGenesisOrganisms(final Context context, final Population population) {
        DequeSet<Organism> genesisOrganisms = new IdentityDequeSet<>();

        fillWithGenesisOrganisms(genesisOrganisms, context, population);

        return genesisOrganisms;
    }

    private static List<SpeciesFitnessStrategy> createSpeciesFitnessStrategies(final Context context) {
        if (!context.parallelism().isEnabled()) {
            return ImmutableList.<SpeciesFitnessStrategy>builder()
                    .add(new SpeciesFitnessStrategyDefault(context.general()))
                    .build();
        }

        return ImmutableList.<SpeciesFitnessStrategy>builder()
                .add(new SpeciesFitnessStrategyUpdateOrganisms(context))
                .add(new SpeciesFitnessStrategyUpdateSpecies())
                .build();
    }

    private static List<SpeciesEvolutionStrategy> createSpeciesEvolutionStrategies(final Context context, final Set<Organism> organismsWithoutSpecies, final OrganismActivator mostFitOrganismActivator) {
        return ImmutableList.<SpeciesEvolutionStrategy>builder()
                .add(new SpeciesEvolutionStrategyRemoveLeastFit(context))
                .add(new SpeciesEvolutionStrategyTotalSharedFitness())
                .add(new SpeciesEvolutionStrategySelectMostElites(context.speciation(), organismsWithoutSpecies))
                .add(new SpeciesEvolutionStrategySelectMostElite(organismsWithoutSpecies, mostFitOrganismActivator))
                .build();
    }

    private static List<SpeciesBreedStrategy> createSpeciesBreedStrategies(final Context context, final Set<Organism> organismsWithoutSpecies, final Queue<OrganismFactory> organismsToBirth) {
        return ImmutableList.<SpeciesBreedStrategy>builder()
                .add(new SpeciesBreedStrategyInterSpecies(context, organismsWithoutSpecies, organismsToBirth))
                .add(new SpeciesBreedStrategyWithinSpecies(context, organismsWithoutSpecies, organismsToBirth))
                .add(new SpeciesBreedStrategyGenesis(context, organismsWithoutSpecies, organismsToBirth))
                .build();
    }

    private boolean addOrganismToFirstCompatibleSpecies(final Organism organism) {
        for (SimpleNode<Species> speciesNode : speciesNodes) {
            if (speciesNodes.getValue(speciesNode).addIfCompatible(context.speciation(), organism)) {
                return true;
            }
        }

        return false;
    }

    private Species createSpecies(final Organism organism) {
        return new Species(context, this, organism);
    }

    private SimpleNode<Species> addSpecies(final Species species) {
        SimpleNode<Species> speciesNode = speciesNodes.createUnbound(species);

        speciesNodes.add(speciesNode);

        return speciesNode;
    }

    private void assignOrganismsToSpecies() {
        Iterable<Organism> organismsNew = organismsToBirth.stream()
                .map(of -> of.create(context))
                ::iterator;

        for (Organism organism : Iterables.concat(organismsWithoutSpecies, organismsNew)) {
            if (!addOrganismToFirstCompatibleSpecies(organism)) {
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

    public void updateFitness() {
        assignOrganismsToSpecies();
        updateFitnessInAllSpecies();
    }

    private void prepareAllSpeciesForEvolution(final SpeciesEvolutionContext evolutionContext) {
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

    private static int countOrganismsInSpecies(final NodeDeque<Species, SimpleNode<Species>> speciesNodes) {
        return speciesNodes.stream()
                .map(speciesNodes::getValue)
                .map(Species::getOrganisms)
                .map(List::size)
                .reduce(0, Integer::sum);
    }

    private static int countOrganismsEverywhere(final Set<Organism> organismsWithoutSpecies, final Queue<OrganismFactory> organismsToBirth, final NodeDeque<Species, SimpleNode<Species>> speciesNodes) {
        return organismsWithoutSpecies.size() + organismsToBirth.size() + countOrganismsInSpecies(speciesNodes);
    }

    public void evolve() {
        SpeciesEvolutionContext evolutionContext = new SpeciesEvolutionContext();

        assert context.general().populationSize() == countOrganismsEverywhere(organismsWithoutSpecies, organismsToBirth, speciesNodes);
        assert organismsToBirth.isEmpty() && context.general().getGenomesKilledCount() == organismsToBirth.size();

        prepareAllSpeciesForEvolution(evolutionContext);

        assert organismsToBirth.isEmpty();

        breedThroughAllSpecies(evolutionContext);
        generation++;

        assert context.general().populationSize() == countOrganismsEverywhere(organismsWithoutSpecies, organismsToBirth, speciesNodes);
        assert context.general().getGenomesKilledCount() == organismsToBirth.size();
    }

    public void restart() {
        context.general().reset();
        context.nodes().reset();
        context.connections().reset();
        organismsWithoutSpecies.clear();
        fillWithGenesisOrganisms(organismsWithoutSpecies, context, this);
        organismsToBirth.clear();
        speciesNodes.clear();
        mostFitOrganismActivator.setOrganism(organismsWithoutSpecies.iterator().next());
        generation = 1;
    }
}