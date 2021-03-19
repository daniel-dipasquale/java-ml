package com.dipasquale.ai.rl.neat.population;

import com.dipasquale.ai.rl.neat.context.Context;
import com.dipasquale.ai.rl.neat.genotype.Organism;
import com.dipasquale.ai.rl.neat.species.Species;
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
    @Getter
    private final OrganismCollectiveStrategy mostFitCollectiveStrategy;
    private final List<SpeciesEvolutionStrategy> speciesEvolutionStrategies;
    private final List<SpeciesBreedStrategy> speciesBreedStrategies;
    private SpeciesBreedContext speciesBreedContext;
    @Getter
    private int generation;

    public Population(final Context context) {
        Set<Organism> organismsWithoutSpecies = createOrganisms(context, this);
        OrganismCollectiveStrategy mostFitCollectiveStrategy = new OrganismCollectiveStrategy(organismsWithoutSpecies.iterator().next());

        List<SpeciesEvolutionStrategy> speciesEvolutionStrategies = ImmutableList.<SpeciesEvolutionStrategy>builder()
                .add(new SpeciesEvolutionStrategyRemoveLeastFit())
                .add(new SpeciesEvolutionStrategyTotalSharedFitness())
                .add(new SpeciesEvolutionStrategySelectElitists(organismsWithoutSpecies))
                .add(new SpeciesEvolutionStrategySelectChampion(organismsWithoutSpecies, mostFitCollectiveStrategy))
                .build();

        List<SpeciesBreedStrategy> speciesBreedStrategies = ImmutableList.<SpeciesBreedStrategy>builder()
                .add(new SpeciesBreedStrategyInterSpecies(context, organismsWithoutSpecies))
                .add(new SpeciesBreedStrategyCrossSpecies(organismsWithoutSpecies))
                .add(new SpeciesBreedStrategyGenesis(organismsWithoutSpecies))
                .build();

        this.context = context;
        this.organismsWithoutSpecies = organismsWithoutSpecies;
        this.allSpecies = new SimpleNodeDeque<>();
        this.mostFitCollectiveStrategy = mostFitCollectiveStrategy;
        this.speciesEvolutionStrategies = speciesEvolutionStrategies;
        this.speciesBreedStrategies = speciesBreedStrategies;
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

    private Species createSpecies(final Organism organism) {
        return new Species(context, this, organism);
    }

    private SimpleNode<Species> addSpecies(final Species species) {
        SimpleNode<Species> speciesNode = allSpecies.createUnbound(species);

        allSpecies.add(speciesNode);

        return speciesNode;
    }

    private void assignOrganismsToSpecies() {
        for (Organism organism : organismsWithoutSpecies) { // TODO: shuffle species distribution
            allSpecies.stream()
                    .filter(n -> allSpecies.getValue(n).addIfCompatible(organism))
                    .findFirst()
                    .orElseGet(() -> {
                        if (allSpecies.size() < context.speciation().maximumSpecies()) {
                            return addSpecies(createSpecies(organism));
                        }

                        String message = String.format("cannot have more species than %d, consider reviewing some parameters or for an easy fix increase the speciation.compatibilityThresholdModifier", context.speciation().maximumSpecies());

                        throw new IllegalStateException(message);
                    });
        }

        organismsWithoutSpecies.clear();
    }

    private void updateFitnessInAllSpecies() {
        for (SimpleNode<Species> speciesNode : allSpecies) {
            allSpecies.getValue(speciesNode).updateFitness();
        }
    }

    public int species() {
        return allSpecies.size();
    }

    public void testFitness() {
        assignOrganismsToSpecies();
        updateFitnessInAllSpecies();
    }

    private void prepareAllSpeciesForEvolution(final SpeciesEvolutionContext context) {
        for (SimpleNode<Species> speciesNode = allSpecies.peekFirst(); speciesNode != null; ) {
            Species species = allSpecies.getValue(speciesNode);

            for (SpeciesEvolutionStrategy speciesEvolutionStrategy : speciesEvolutionStrategies) {
                speciesEvolutionStrategy.process(context, species);
            }

            if (!species.shouldSurvive()) {
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