/*
 * java-ml
 * (c) 2021 daniel-dipasquale
 * released under the MIT license
 */

package com.dipasquale.ai.rl.neat.speciation.strategy.evolution;

import com.dipasquale.ai.rl.neat.context.Context;
import com.dipasquale.ai.rl.neat.speciation.core.Species;
import com.dipasquale.ai.rl.neat.speciation.organism.Organism;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@RequiredArgsConstructor
public final class RemoveLeastFitSpeciesEvolutionStrategy implements SpeciesEvolutionStrategy, Serializable {
    @Serial
    private static final long serialVersionUID = -8972350994440640462L;
    private final Context context;

    @Override
    public void process(final SpeciesEvolutionContext evolutionContext, final Species species, boolean speciesSurvives) {
        if (speciesSurvives) {
            species.removeUnfitToReproduce(context.speciation()).forEach(Organism::kill);
        } else {
            species.getOrganisms().forEach(Organism::kill);
        }
    }

    @Override
    public void postProcess(final SpeciesEvolutionContext evolutionContext) {
    }
}
