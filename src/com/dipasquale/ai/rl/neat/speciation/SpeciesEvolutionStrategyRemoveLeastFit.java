package com.dipasquale.ai.rl.neat.speciation;

import com.dipasquale.ai.rl.neat.context.Context;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class SpeciesEvolutionStrategyRemoveLeastFit implements SpeciesEvolutionStrategy, Serializable {
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
