package com.dipasquale.ai.rl.neat.population;

import com.dipasquale.ai.rl.neat.context.Context;
import com.dipasquale.ai.rl.neat.genotype.Organism;
import com.dipasquale.ai.rl.neat.species.Species;
import com.dipasquale.ai.rl.neat.species.SpeciesDefault;
import com.dipasquale.data.structure.deque.NodeDeque;
import com.dipasquale.data.structure.deque.SimpleNode;
import com.dipasquale.data.structure.deque.SimpleNodeDeque;
import com.google.common.collect.ImmutableList;
import lombok.Getter;

import java.util.Collections;
import java.util.Comparator;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public final class Population {
    private static final Comparator<Species> SHARED_FITNESS_COMPARATOR = Comparator.comparing(Species::getSharedFitness);
    private final Context context;
    private final Set<Organism> organismsWithoutSpecies;
    private final NodeDeque<Species, SimpleNode<Species>> allSpecies;
    private final List<SpeciesFitnessStrategy> speciesFitnessStrategies;
    private final List<SpeciesEvolutionStrategy> speciesEvolutionStrategies;
    private final List<SpeciesBreedStrategy> speciesBreedStrategies;
    private SpeciesBreedContext speciesBreedContext;
    @Getter
    private int generation;

    public Population(final Context context, final OrganismActivator mostFitOrganismActivator) {
        Set<Organism> organismsWithoutSpecies = createOrganisms(context, this);

        mostFitOrganismActivator.setOrganism(organismsWithoutSpecies.iterator().next());
        this.context = context;
        this.organismsWithoutSpecies = organismsWithoutSpecies;
        this.allSpecies = new SimpleNodeDeque<>();
        this.speciesFitnessStrategies = createSpeciesFitnessStrategies(context);
        this.speciesEvolutionStrategies = createSpeciesEvolutionStrategies(organismsWithoutSpecies, mostFitOrganismActivator);
        this.speciesBreedStrategies = createSpeciesBreedStrategies(context, organismsWithoutSpecies);
        this.generation = 1;
    }

    private static Set<Organism> createOrganisms(final Context context, final Population population) {
        Set<Organism> organismsWithoutSpecies = Collections.newSetFromMap(new IdentityHashMap<>());

        IntStream.range(0, context.general().populationSize())
                .mapToObj(i -> context.general().createGenesisGenome())
                .map(g -> new Organism(context, population, g))
                .forEach(organismsWithoutSpecies::add);

        return organismsWithoutSpecies;
    }

    private static List<SpeciesFitnessStrategy> createSpeciesFitnessStrategies(final Context context) {
        if (!context.parallelism().isEnabled()) {
            return ImmutableList.<SpeciesFitnessStrategy>builder()
                    .add(new SpeciesFitnessStrategyDefault())
                    .build();
        }

        return ImmutableList.<SpeciesFitnessStrategy>builder()
                .add(new SpeciesFitnessStrategyUpdateOrganisms(context))
                .add(new SpeciesFitnessStrategyUpdateSpecies())
                .build();
    }

    private static List<SpeciesEvolutionStrategy> createSpeciesEvolutionStrategies(final Set<Organism> organismsWithoutSpecies, final OrganismActivator mostFitOrganismActivator) {
        return ImmutableList.<SpeciesEvolutionStrategy>builder()
                .add(new SpeciesEvolutionStrategyRemoveLeastFit())
                .add(new SpeciesEvolutionStrategyTotalSharedFitness())
                .add(new SpeciesEvolutionStrategySelectMostElites(organismsWithoutSpecies))
                .add(new SpeciesEvolutionStrategySelectMostElite(organismsWithoutSpecies, mostFitOrganismActivator))
                .build();
    }

    private static List<SpeciesBreedStrategy> createSpeciesBreedStrategies(final Context context, final Set<Organism> organismsWithoutSpecies) {
        return ImmutableList.<SpeciesBreedStrategy>builder()
                .add(new SpeciesBreedStrategyInterSpecies(context, organismsWithoutSpecies))
                .add(new SpeciesBreedStrategyWithinSpecies(context, organismsWithoutSpecies))
                .add(new SpeciesBreedStrategyGenesis(organismsWithoutSpecies))
                .build();
    }

    private Species createSpecies(final Organism organism) {
        return new SpeciesDefault(context, this, organism);
    }

    private SimpleNode<Species> addSpecies(final Species species) {
        SimpleNode<Species> speciesNode = allSpecies.createUnbound(species);

        allSpecies.add(speciesNode);

        return speciesNode;
    }

    private boolean addOrganismToMostCompatibleSpecies(final Organism organism) {
        for (SimpleNode<Species> speciesNode : allSpecies) {
            if (allSpecies.getValue(speciesNode).addIfCompatible(organism)) {
                return true;
            }
        }

        return false;
    }

    private void assignOrganismsToSpecies() {
        for (Organism organism : organismsWithoutSpecies) {
            if (!addOrganismToMostCompatibleSpecies(organism)) {
                if (allSpecies.size() < context.speciation().maximumSpecies()) {
                    addSpecies(createSpecies(organism));
                } else {
                    organism.getMostCompatibleSpecies().add(organism);
                }
            }
        }

        organismsWithoutSpecies.clear();
    }

    private void updateFitnessInAllSpecies() {
        for (SpeciesFitnessStrategy speciesFitnessStrategy : speciesFitnessStrategies) {
            speciesFitnessStrategy.process(allSpecies);
        }
    }

    public int getSpeciesCount() {
        return allSpecies.size();
    }

    public void updateFitness() {
        assignOrganismsToSpecies();
        updateFitnessInAllSpecies();
    }

    private void prepareAllSpeciesForEvolution(final SpeciesEvolutionContext context) {
        for (SimpleNode<Species> speciesNode = allSpecies.peekFirst(); speciesNode != null; ) {
            Species species = allSpecies.getValue(speciesNode);
            boolean shouldSurvive = allSpecies.size() <= 2 || species.shouldSurvive();

            for (SpeciesEvolutionStrategy speciesEvolutionStrategy : speciesEvolutionStrategies) {
                speciesEvolutionStrategy.process(context, species, shouldSurvive);
            }

            if (!shouldSurvive) {
                SimpleNode<Species> speciesNodeNext = allSpecies.peekNext(speciesNode);

                allSpecies.remove(speciesNode);
                speciesNode = speciesNodeNext;
            } else {
                speciesNode = allSpecies.peekNext(speciesNode);
            }
        }

        for (SpeciesEvolutionStrategy speciesEvolutionStrategy : speciesEvolutionStrategies) {
            speciesEvolutionStrategy.postProcess(context);
        }
    }

    private void breedThroughAllSpecies(final SpeciesEvolutionContext context) {
        if (speciesBreedContext == null) {
            speciesBreedContext = new SpeciesBreedContext(context);
        } else {
            speciesBreedContext = new SpeciesBreedContext(context, speciesBreedContext.getInterSpeciesBreedingLeftOverRatio());
        }

        List<Species> allSpeciesList = allSpecies.stream()
                .map(allSpecies::getValue)
                .sorted(SHARED_FITNESS_COMPARATOR)
                .collect(Collectors.toList());

        for (SpeciesBreedStrategy speciesBreedStrategy : speciesBreedStrategies) {
            speciesBreedStrategy.process(speciesBreedContext, allSpeciesList);
        }
    }

    public void evolve() {
        SpeciesEvolutionContext context = new SpeciesEvolutionContext();

        prepareAllSpeciesForEvolution(context);
        breedThroughAllSpecies(context);
        generation++;
    }
}