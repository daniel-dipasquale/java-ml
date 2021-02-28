package com.dipasquale.ai.rl.neat;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class ContextDefaultCrossOver<T extends Comparable<T>> implements Context.CrossOver<T> {
    private final float rate;
    private final float enforceExpressedRate;
    private final float useRandomParentWeightRate;
    private final GenomeCrossOver<T> genomeCrossOver;

    @Override
    public float rate() {
        return rate;
    }

    @Override
    public float enforceExpressedRate() {
        return enforceExpressedRate;
    }

    @Override
    public float useRandomParentWeightRate() {
        return useRandomParentWeightRate;
    }

    @Override
    public GenomeDefault<T> crossOverBySkippingUnfitDisjointOrExcess(final GenomeDefault<T> fitParent, final GenomeDefault<T> unfitParent) {
        return genomeCrossOver.crossOverBySkippingUnfitDisjointOrExcess(fitParent, unfitParent);
    }

    @Override
    public GenomeDefault<T> crossOverByEqualTreatment(final GenomeDefault<T> parent1, final GenomeDefault<T> parent2) {
        return genomeCrossOver.crossOverByEqualTreatment(parent1, parent2);
    }
}
