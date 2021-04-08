package com.dipasquale.ai.rl.neat.population;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

@Getter
final class SpeciesBreedContext implements Serializable {
    @Serial
    private static final long serialVersionUID = -368081389919247260L;
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
