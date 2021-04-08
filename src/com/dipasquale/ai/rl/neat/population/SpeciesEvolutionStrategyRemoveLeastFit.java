package com.dipasquale.ai.rl.neat.population;

import com.dipasquale.ai.rl.neat.context.Context;
import com.dipasquale.ai.rl.neat.genotype.Species;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.io.Serial;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class SpeciesEvolutionStrategyRemoveLeastFit implements SpeciesEvolutionStrategy {
    @Serial
    private static final long serialVersionUID = -8513956335859361822L;
    private final Context context;

    @Override
    public void process(final SpeciesEvolutionContext evolutionContext, final Species species, boolean speciesSurvives) {
        Context.GeneralSupport general = context.general();

        if (speciesSurvives) {
            species.removeUnfitToReproduce(context.speciation()).forEach(o -> o.kill(general));
        } else {
            species.getOrganisms().forEach(o -> o.kill(general));
        }
    }

    @Override
    public void postProcess(final SpeciesEvolutionContext evolutionContext) {
    }
}
