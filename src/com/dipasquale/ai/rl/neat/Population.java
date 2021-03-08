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
import java.util.stream.Collectors;
import java.util.stream.IntStream;

final class Population implements NeatCollective {
    private static final Comparator<Species> SHARED_FITNESS_COMPARATOR = Comparator.comparing(Species::getSharedFitness);
    private final Context context;
    private final Set<Organism> organismsWithoutSpecies;
    private final NodeQueue<Species> allSpecies;
    @Getter
    private int generation;
    private float interspeciesMatingUnusedSpace;

    Population(final Context context) {
        this.context = context;
        this.organismsWithoutSpecies = createOrganisms(context, this);
        this.allSpecies = NodeQueue.create();
        this.generation = 1;
        this.interspeciesMatingUnusedSpace = 0f;
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

    private Node addSpecies(final Species species) {
        Node speciesNode = allSpecies.createUnbound(species);

        allSpecies.add(speciesNode);

        return speciesNode;
    }

    private void assignOrganismsToSpecies() {
        for (Organism organism : organismsWithoutSpecies) {
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

    @Override
    public int generation() {
        return generation;
    }

    @Override
    public int species() {
        return allSpecies.size();
    }

    @Override
    public void testFitness() {
        assignOrganismsToSpecies();
        updateFitnessInAllSpecies();
    }

    private LeastFitOrStagnantResult removeLeastFitOrganismsOrStagnantSpecies() {
        int organismsRemoved = 0;
        float totalSharedFitness = 0f;

        for (Node speciesNode = allSpecies.first(); speciesNode != null; ) {
            Species species = allSpecies.getValue(speciesNode);

            if (species.shouldSurvive()) {
                List<Organism> unfitOrganisms = species.removeUnfitToReproduce();

                organismsRemoved += unfitOrganisms.size() + species.size() - 1;
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
            List<Organism> eliteOrganisms = allSpecies.getValue(speciesNode).selectElitists();

            organismsSaved += eliteOrganisms.size();
            organismsWithoutSpecies.addAll(eliteOrganisms);
        }

        return organismsSaved;
    }

    private float breedInterspecies(final List<Species> speciesList, final float spaceAvailable) {
        if (speciesList.size() <= 1) {
            return 0;
        }

        float spaceOccupied = spaceAvailable * context.speciation().interspeciesMatingRate() + interspeciesMatingUnusedSpace;
        int spaceOccupiedFixed = (int) Math.floor(spaceOccupied);

        interspeciesMatingUnusedSpace = spaceOccupied - (float) spaceOccupiedFixed;

        for (int i = 0; i < spaceOccupiedFixed; i++) {
            Species species1 = context.random().nextItem(speciesList);
            Species species2 = context.random().nextItem(speciesList);

            organismsWithoutSpecies.add(species1.reproduceOutcast(species2));
        }

        return (float) spaceOccupiedFixed;
    }

    private void breedAndRestartAllSpecies(final int spaceAvailable, final float totalSharedFitness) {
        List<Species> allSpeciesList = allSpecies.stream()
                .map(allSpecies::getValue)
                .sorted(SHARED_FITNESS_COMPARATOR)
                .collect(Collectors.toList());

        float spaceAvailableFloat = (float) spaceAvailable;
        float spaceAvailableSameSpecies = spaceAvailableFloat - breedInterspecies(allSpeciesList, spaceAvailableFloat);
        float organismsReproducedPrevious;
        float organismsReproduced = 0f;

        for (Species species : allSpeciesList) {
            float reproductionFloat = Math.round(spaceAvailableSameSpecies * species.getSharedFitness() / totalSharedFitness);

            organismsReproducedPrevious = organismsReproduced;
            organismsReproduced += reproductionFloat;

            int reproduction = (int) organismsReproduced - (int) organismsReproducedPrevious;
            List<Organism> reproducedOrganisms = species.reproduceOutcast(reproduction);

            organismsWithoutSpecies.addAll(reproducedOrganisms);
            species.restart();
            organismsWithoutSpecies.remove(species.getRepresentative());
        }
    }

    @Override
    public void evolve() {
        LeastFitOrStagnantResult leastFitOrStagnantResult = removeLeastFitOrganismsOrStagnantSpecies();
        int organismsPreserved = preserveElitesFromAllSpecies();
        int spaceAvailable = leastFitOrStagnantResult.organismsRemoved - organismsPreserved;

        breedAndRestartAllSpecies(spaceAvailable, leastFitOrStagnantResult.totalSharedFitnessRemaining);
        generation++;
    }

    @RequiredArgsConstructor(access = AccessLevel.PACKAGE)
    private static final class LeastFitOrStagnantResult {
        private final int organismsRemoved;
        private final float totalSharedFitnessRemaining;
    }
}