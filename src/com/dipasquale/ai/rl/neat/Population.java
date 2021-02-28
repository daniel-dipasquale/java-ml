package com.dipasquale.ai.rl.neat;

import com.dipasquale.data.structure.queue.Node;
import com.dipasquale.data.structure.queue.NodeQueue;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.IntStream;

final class Population<T extends Comparable<T>> {
    private final Context<T> context;
    private final Set<Organism<T>> organismsWithoutSpecies;
    private final NodeQueue<Species<T>> allSpecies;
    @Getter
    private int generation;

    Population(final Context<T> context) {
        this.context = context;
        this.organismsWithoutSpecies = createOrganisms(context, this);
        this.allSpecies = NodeQueue.create();
        this.generation = 1;
    }

    private static <T extends Comparable<T>> Set<Organism<T>> createOrganisms(final Context<T> context, final Population<T> population) {
        IdentityHashMap<Organism<T>, Boolean> organismsWithoutSpecies = IntStream.range(0, context.general().populationSize())
                .mapToObj(i -> context.general().createGenesisGenome(0))
                .map(g -> new Organism<>(context, population, g))
                .collect(Collector.of(IdentityHashMap::new, (ihm, g) -> ihm.put(g, null), (ihm1, ihm2) -> {
                    ihm1.putAll(ihm2);

                    return ihm1;
                }));

        return Collections.newSetFromMap(organismsWithoutSpecies);
    }

    private Species<T> createSpecies(final Organism<T> organism) {
        return new Species<>(context, this, organism);
    }

    private Node addSpecies(final Species<T> species) {
        Node speciesNode = allSpecies.createUnbound(species);

        allSpecies.add(speciesNode);

        return speciesNode;
    }

    private void assignOrganismsToSpecies() {
        for (Organism<T> organism : organismsWithoutSpecies) {
            allSpecies.stream()
                    .filter(n -> allSpecies.getValue(n).addIfCompatible(organism))
                    .findFirst()
                    .orElseGet(() -> addSpecies(createSpecies(organism)));
        }

        organismsWithoutSpecies.clear();
    }

    private void updateFitnessInAllSpecies() {
        for (Node speciesNode : allSpecies) {
            allSpecies.getValue(speciesNode).updateFitness();
        }
    }

    public void testFitness() {
        assignOrganismsToSpecies();
        updateFitnessInAllSpecies();
    }

    private LeastFitOrStagnantResult removeLeastFitOrganismsOrStagnantSpecies() {
        int organismsRemoved = 0;
        float totalSharedFitness = 0f;

        for (Node speciesNode = allSpecies.first(); speciesNode != null; ) {
            Species<T> species = allSpecies.getValue(speciesNode);

            if (species.shouldSurvive()) {
                List<Organism<T>> unfitOrganisms = species.removeUnfitToReproduce();

                organismsRemoved += unfitOrganisms.size();
                totalSharedFitness += species.getSharedFitness();
                speciesNode = allSpecies.next(speciesNode);
            } else {
                organismsRemoved += species.size();

                Node speciesNodeNext = allSpecies.next(speciesNode);

                allSpecies.remove(speciesNode);
                speciesNode = speciesNodeNext;
            }
        }

        return new LeastFitOrStagnantResult(organismsRemoved, totalSharedFitness);
    }

    @RequiredArgsConstructor(access = AccessLevel.PACKAGE)
    private static final class LeastFitOrStagnantResult {
        private final int organismsRemoved;
        private final float totalSharedFitnessRemaining;
    }

    private int preserveElitesFromAllSpecies() {
        int organismsSaved = 0;

        for (Node speciesNode : allSpecies) {
            List<Organism<T>> eliteOrganisms = allSpecies.getValue(speciesNode).selectElitists();

            organismsSaved += eliteOrganisms.size();
            organismsWithoutSpecies.addAll(eliteOrganisms);
        }

        return organismsSaved;
    }

    private int breedInAllSpecies(final int spaceAvailable, final float totalSharedFitness) {
        float spaceAvailableFloat = (float) spaceAvailable;
        int organismsReproduced = 0;

        for (Node speciesNode : allSpecies) {
            Species<T> species = allSpecies.getValue(speciesNode);
            float reproductionFloat = Math.round(spaceAvailable * species.getSharedFitness() / totalSharedFitness);
            int reproduction = Math.min(spaceAvailable - organismsReproduced, (int) reproductionFloat);
            List<Organism<T>> reproducedOrganisms = species.reproduceOutcast(reproduction);

            organismsReproduced += reproducedOrganisms.size();
            organismsWithoutSpecies.addAll(reproducedOrganisms);
        }

        return organismsReproduced;
    }

    public void evolve() {
        generation++;

        LeastFitOrStagnantResult leastFitOrStagnantResult = removeLeastFitOrganismsOrStagnantSpecies();

        int organismsPreserved = preserveElitesFromAllSpecies();

        breedInAllSpecies(context.general().populationSize() - leastFitOrStagnantResult.organismsRemoved + organismsPreserved, leastFitOrStagnantResult.totalSharedFitnessRemaining);
    }
}