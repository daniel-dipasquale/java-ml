package com.experimental.ai.rl.neat;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.IntStream;

final class Population<T extends Comparable<T>> {
    private final Context<T> context;
    private final Genome<T> genesisGenome;
    private final Set<Organism<T>> organismsWithoutSpecies;
//    private final Map<Organism<T>, Species<T>> organismsToSpecies;
    private final List<Species<T>> allSpecies;
    @Getter
    private int generation;

    Population(final Context<T> context) {
        Genome<T> genesisGenome = context.general().createGenesisGenome();

        this.context = context;
        this.genesisGenome = genesisGenome;
        this.organismsWithoutSpecies = createOrganisms(context, genesisGenome);
//        this.organismsToSpecies = new IdentityHashMap<>();
        this.allSpecies = new ArrayList<>();
        this.generation = 1;
    }

    private static <T extends Comparable<T>> Set<Organism<T>> createOrganisms(final Context<T> context, final Genome<T> genesisGenome) {
        IdentityHashMap<Organism<T>, Boolean> organismsWithoutSpecies = IntStream.range(0, context.general().populationSize())
                .mapToObj(i -> genesisGenome.createCopy())
                .map(g -> new Organism<>(context.general(), g))
                .collect(Collector.of(IdentityHashMap::new, (ihm, g) -> ihm.put(g, null), (ihm1, ihm2) -> {
                    ihm1.putAll(ihm2);

                    return ihm1;
                })); // TODO: experiment with mutations here

        return Collections.newSetFromMap(organismsWithoutSpecies);
    }

    private Species<T> createSpecies(final Organism<T> organism) {
        Species<T> species = new Species<>(context, this, organism);

        allSpecies.add(species);

        return species;
    }

    private void assignOrganismsToSpecies() {
        for (Organism<T> organism : organismsWithoutSpecies) {
            Species<T> species = allSpecies.stream()
                    .filter(s -> s.addIfCompatible(organism))
                    .findFirst()
                    .orElseGet(() -> {
                        Species<T> speciesNew = createSpecies(organism);

                        allSpecies.add(speciesNew);

                        return speciesNew;
                    });

//            organismsToSpecies.put(organism, species);
        }

        organismsWithoutSpecies.clear();
    }

    private void stepIntoGeneration() {
        int organismsRemoved = 0;

        for (Species<T> species : allSpecies) {
            species.adjustFitness();

            List<Organism<T>> unfitOrganisms = species.removeLeastFit();
//            int breed = unfitOrganisms.size();
//
//            organismsToSpecies.keySet().removeAll(unfitOrganisms);
//            organismsWithoutSpecies.addAll(species.breedOrMutateOutcast(breed));
//
//            Collection<Organism<T>> organismsCleared = species.restart();
//
//            organismsToSpecies.keySet().removeAll(organismsCleared);
//            organismsWithoutSpecies.addAll(organismsCleared);
            organismsRemoved += unfitOrganisms.size();
        }
    }

    public void evolve() {
        assignOrganismsToSpecies();
        stepIntoGeneration();
        generation++;
    }
}
