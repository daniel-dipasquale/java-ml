package com.dipasquale.ai.rl.neat;

import com.dipasquale.data.structure.queue.Node;
import com.dipasquale.data.structure.queue.NodeQueue;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Collections;
import java.util.Comparator;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

final class Population<T extends Comparable<T>> {
    private final Context<T> context;
    private final Set<Organism<T>> organismsWithoutSpecies;
    private final NodeQueue<Species<T>> allSpecies;
    private final Comparator<Species<T>> sharedFitnessComparator;
    @Getter
    private int generation;

    Population(final Context<T> context) {
        this.context = context;
        this.organismsWithoutSpecies = createOrganisms(context, this);
        this.allSpecies = NodeQueue.create();
        this.sharedFitnessComparator = Comparator.comparing(Species::getSharedFitness);
        this.generation = 1;
    }

    private static <T extends Comparable<T>> Set<Organism<T>> createOrganisms(final Context<T> context, final Population<T> population) {
        IdentityHashMap<Organism<T>, Boolean> organismsWithoutSpecies = IntStream.range(0, context.general().populationSize())
                .mapToObj(i -> context.general().createGenesisGenome())
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
                Node speciesNodeNext = allSpecies.next(speciesNode);

                organismsRemoved += species.size();
                allSpecies.remove(speciesNode);
                speciesNode = speciesNodeNext;
            }
        }

        return new LeastFitOrStagnantResult(organismsRemoved, totalSharedFitness);
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

    private void breedAndRestartAllSpecies(final int spaceAvailable, final float totalSharedFitness) {
        List<Species<T>> allSpeciesList = allSpecies.stream()
                .map(allSpecies::getValue)
                .sorted(sharedFitnessComparator)
                .collect(Collectors.toList());

        float spaceAvailableFloat = (float) spaceAvailable;
        int spaceAvailableInterspecies = allSpecies.size() <= 1 ? 0 : (int) Math.floor(spaceAvailableFloat * context.speciation().interspeciesMatingRate());

        for (int i = 0; i < spaceAvailableInterspecies; i++) {
            Species<T> species1 = context.random().nextItem(allSpeciesList);
            Species<T> species2 = context.random().nextItem(allSpeciesList);

            organismsWithoutSpecies.add(species1.reproduceOutcast(species2));
        }

        float spaceAvailableSameSpecies = spaceAvailableFloat - (float) spaceAvailableInterspecies;
        float organismsReproducedPrevious;
        float organismsReproduced = 0f;

        for (Species<T> species : allSpeciesList) {
            float reproductionFloat = Math.round(spaceAvailableSameSpecies * species.getSharedFitness() / totalSharedFitness);

            organismsReproducedPrevious = organismsReproduced;
            organismsReproduced += reproductionFloat;

            int reproduction = (int) organismsReproduced - (int) organismsReproducedPrevious;
            List<Organism<T>> reproducedOrganisms = species.reproduceOutcast(reproduction);

            organismsWithoutSpecies.addAll(reproducedOrganisms);
            organismsWithoutSpecies.remove(species.restart());
        }
    }

    public void evolve() {
        LeastFitOrStagnantResult leastFitOrStagnantResult = removeLeastFitOrganismsOrStagnantSpecies();
        int organismsPreserved = preserveElitesFromAllSpecies();
        int spaceAvailable = context.general().populationSize() - leastFitOrStagnantResult.organismsRemoved + organismsPreserved;

        breedAndRestartAllSpecies(spaceAvailable, leastFitOrStagnantResult.totalSharedFitnessRemaining);
        generation++;
    }

    @RequiredArgsConstructor(access = AccessLevel.PACKAGE)
    private static final class LeastFitOrStagnantResult {
        private final int organismsRemoved;
        private final float totalSharedFitnessRemaining;
    }
}