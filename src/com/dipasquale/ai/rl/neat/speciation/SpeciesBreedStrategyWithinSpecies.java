package com.dipasquale.ai.rl.neat.speciation;

import com.dipasquale.ai.rl.neat.context.Context;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.util.List;
import java.util.Queue;
import java.util.Set;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class SpeciesBreedStrategyWithinSpecies implements SpeciesBreedStrategy {
    @Serial
    private static final long serialVersionUID = 3643460782078459074L;
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
