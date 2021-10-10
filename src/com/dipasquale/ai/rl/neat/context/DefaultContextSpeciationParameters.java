package com.dipasquale.ai.rl.neat.context;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PACKAGE)
public final class DefaultContextSpeciationParameters implements Context.SpeciationParameters, Serializable {
    @Serial
    private static final long serialVersionUID = -7007214942183273340L;
    private final int maximumSpecies;
    private final float compatibilityThreshold;
    private final float compatibilityThresholdModifier;
    private final float eugenicsThreshold;
    private final float elitistThreshold;
    private final int elitistThresholdMinimum;
    private final int stagnationDropOffAge;
    private final float interSpeciesMatingRate;

    @Override
    public int maximumSpecies() {
        return maximumSpecies;
    }

    @Override
    public double compatibilityThreshold(final int generation) {
        return compatibilityThreshold * Math.pow(compatibilityThresholdModifier, generation);
    }

    @Override
    public float eugenicsThreshold() {
        return eugenicsThreshold;
    }

    @Override
    public float elitistThreshold() {
        return elitistThreshold;
    }

    @Override
    public int elitistThresholdMinimum() {
        return elitistThresholdMinimum;
    }

    @Override
    public int stagnationDropOffAge() {
        return stagnationDropOffAge;
    }

    @Override
    public float interSpeciesMatingRate() {
        return interSpeciesMatingRate;
    }
}
