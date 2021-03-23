package com.dipasquale.ai.rl.neat.population;

import lombok.Getter;
import lombok.Setter;

@Getter
final class SpeciesBreedContext {
    private final float totalSharedFitness;
    @Setter
    private float interSpeciesBreedingLeftOverRatio;

    SpeciesBreedContext(final SpeciesEvolutionContext context, final float interSpeciesBreedingLeftOverRatio) {
        this.totalSharedFitness = context.getTotalSharedFitness();
        this.interSpeciesBreedingLeftOverRatio = interSpeciesBreedingLeftOverRatio;
    }

    SpeciesBreedContext(final SpeciesEvolutionContext context) {
        this(context, 0f);
    }
}
