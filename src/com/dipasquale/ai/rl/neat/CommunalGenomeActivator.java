package com.dipasquale.ai.rl.neat;

import com.dipasquale.ai.rl.neat.phenotype.GenomeActivator;
import com.dipasquale.common.FloatValue;
import com.dipasquale.data.structure.collection.IterableArray;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public final class CommunalGenomeActivator {
    @Getter(AccessLevel.PACKAGE)
    private final Context context;
    @Getter
    private final List<GenomeActivator> genomeActivators;
    private final IterableArray<FloatValue> fitnessValues;

    float getFitness(final GenomeActivator genomeActivator) {
        return fitnessValues.get(genomeActivator.getGenome().getId()).current();
    }

    public void addFitness(final GenomeActivator genomeActivator, final float fitness) {
        fitnessValues.get(genomeActivator.getGenome().getId()).increment(fitness);
    }
}
