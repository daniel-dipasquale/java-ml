package com.dipasquale.ai.rl.neat.core;

import com.dipasquale.ai.rl.neat.phenotype.GenomeActivator;
import com.dipasquale.synchronization.dual.mode.DualModeFloatValue;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public final class SharedGenomeActivator {
    @Getter(AccessLevel.PACKAGE)
    private final Context context;
    @Getter
    private final List<GenomeActivator> genomeActivators;
    private final Map<GenomeActivator, DualModeFloatValue> fitnessValues;

    float getFitness(final GenomeActivator genomeActivator) {
        return fitnessValues.get(genomeActivator).current();
    }

    public void addFitness(final GenomeActivator genomeActivator, final float fitness) {
        fitnessValues.get(genomeActivator).increment(fitness);
    }
}
