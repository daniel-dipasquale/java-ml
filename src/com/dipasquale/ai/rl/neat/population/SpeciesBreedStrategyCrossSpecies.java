package com.dipasquale.ai.rl.neat.population;

import com.dipasquale.ai.rl.neat.context.Context;
import com.dipasquale.ai.rl.neat.genotype.Organism;
import com.dipasquale.ai.rl.neat.species.Species;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Set;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class SpeciesBreedStrategyCrossSpecies implements SpeciesBreedStrategy {
    private final Context neatContext;
    private final Set<Organism> organismsWithoutSpecies;

    @Override
    public void process(final SpeciesBreedContext context, final List<Species> speciesList) {
        int populationSize = neatContext.general().populationSize();
        int speciesSize = speciesList.size();
        float organismsNeeded = (float) (populationSize - speciesSize - organismsWithoutSpecies.size());
        float organismsReproducedPrevious;
        float organismsReproduced = 0f;

        for (Species species : speciesList) {
            float reproductionFloat = organismsNeeded * species.getSharedFitness() / context.getTotalSharedFitness();

            organismsReproducedPrevious = organismsReproduced;
            organismsReproduced += reproductionFloat;

            int reproduction = (int) organismsReproduced - (int) organismsReproducedPrevious;
            List<Organism> reproducedOrganisms = species.reproduceOutcast(reproduction);

            organismsWithoutSpecies.addAll(reproducedOrganisms);
        }

        int organismsNeededStill = populationSize - speciesSize - organismsWithoutSpecies.size();

        if (organismsNeededStill > 0) { // NOTE: floating point problem
            Species species = speciesList.get(speciesList.size() - 1);
            List<Organism> reproducedOrganisms = species.reproduceOutcast(organismsNeededStill);

            organismsWithoutSpecies.addAll(reproducedOrganisms);
        }
    }
}
