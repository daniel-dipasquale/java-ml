package com.dipasquale.ai.rl.neat.speciation.strategy.breeding;

import com.dipasquale.ai.rl.neat.context.Context;
import com.dipasquale.ai.rl.neat.speciation.core.Species;
import com.dipasquale.ai.rl.neat.speciation.organism.Organism;
import com.dipasquale.ai.rl.neat.speciation.organism.OrganismFactory;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.Queue;
import java.util.Set;

@RequiredArgsConstructor
public final class MatchingSpeciesBreedingStrategy implements SpeciesBreedingStrategy, Serializable {
    @Serial
    private static final long serialVersionUID = 3643460782078459074L;
    private final Context context;
    private final Set<Organism> organismsWithoutSpecies;
    private final Queue<OrganismFactory> organismsToBirth;

    @Override
    public void process(final SpeciesBreedingContext breedContext, final List<Species> speciesList) {
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
