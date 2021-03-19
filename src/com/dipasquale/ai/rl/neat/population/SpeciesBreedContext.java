package com.dipasquale.ai.rl.neat.population;

import lombok.Getter;
import lombok.Setter;

@Getter
final class SpeciesBreedContext {
    private int organismsNeeded;
    private final float totalSharedFitness;
    @Setter
    private float interSpeciesBreedingLeftOverRatio;

    SpeciesBreedContext(final SpeciesEvolutionContext context, final float interSpeciesBreedingLeftOverRatio) {
        this.organismsNeeded = context.getOrganismsNeeded();
        this.totalSharedFitness = context.getTotalSharedFitness();
        this.interSpeciesBreedingLeftOverRatio = interSpeciesBreedingLeftOverRatio;
    }

    SpeciesBreedContext(final SpeciesEvolutionContext context) {
        this(context, 0f);
    }

    public void addOrganismsNeeded(final int delta) {
        organismsNeeded += delta;
    }
}
