package com.dipasquale.ai.rl.neat.population;

import com.dipasquale.ai.rl.neat.context.Context;
import com.dipasquale.ai.rl.neat.genotype.Organism;
import com.dipasquale.ai.rl.neat.genotype.OrganismFactory;
import com.dipasquale.ai.rl.neat.species.Species;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Queue;
import java.util.Set;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class SpeciesBreedStrategyWithinSpecies implements SpeciesBreedStrategy {
    private final Context context;
    private final Set<Organism> organismsWithoutSpecies;
    private final Queue<OrganismFactory> organismsToBirth;

    @Override
    public void process(final SpeciesBreedContext breedContext, final List<Species> speciesList) {
        int populationSize = context.general().populationSize();
        int speciesSize = speciesList.size();
        float organismsNeeded = (float) (populationSize - speciesSize - organismsWithoutSpecies.size() - organismsToBirth.size());
        float organismsReproducedPrevious;
        float organismsReproduced = 0f;

        for (Species species : speciesList) {
            float reproductionFloat = organismsNeeded * species.getSharedFitness() / breedContext.getTotalSharedFitness();

            organismsReproducedPrevious = organismsReproduced;
            organismsReproduced += reproductionFloat;

            int reproduction = (int) organismsReproduced - (int) organismsReproducedPrevious;

            organismsToBirth.addAll(species.getOrganismsToBirth(context, reproduction));
        }

        int organismsNeededStill = populationSize - speciesSize - organismsWithoutSpecies.size() - organismsToBirth.size();

        if (organismsNeededStill > 0) { // NOTE: floating point problem
            Species species = speciesList.get(speciesList.size() - 1);

            organismsToBirth.addAll(species.getOrganismsToBirth(context, organismsNeededStill));
        }
    }
}
