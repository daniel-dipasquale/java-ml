package com.dipasquale.ai.rl.neat.speciation.strategy.breeding;

import com.dipasquale.ai.rl.neat.speciation.strategy.evolution.SpeciesEvolutionContext;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

@Getter
public final class SpeciesBreedingContext implements Serializable {
    @Serial
    private static final long serialVersionUID = -368081389919247260L;
    private final float totalSharedFitness;
    @Setter
    private float interSpeciesBreedingLeftOverRatio;

    public SpeciesBreedingContext(final SpeciesEvolutionContext context, final float interSpeciesBreedingLeftOverRatio) {
        this.totalSharedFitness = context.getTotalSharedFitness();
        this.interSpeciesBreedingLeftOverRatio = interSpeciesBreedingLeftOverRatio;
    }

    public SpeciesBreedingContext(final SpeciesEvolutionContext context) {
        this(context, 0f);
    }
}
