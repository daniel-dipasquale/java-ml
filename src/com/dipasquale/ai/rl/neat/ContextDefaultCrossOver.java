package com.dipasquale.ai.rl.neat;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class ContextDefaultCrossOver implements Context.CrossOver {
    private final float rate;
    private final float enforceExpressedRate;
    private final float useRandomParentWeightRate;
    private final GenomeCrossOver genomeCrossOver;

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
    public GenomeDefault crossOverBySkippingUnfitDisjointOrExcess(final GenomeDefault fitParent, final GenomeDefault unfitParent) {
        return genomeCrossOver.crossOverBySkippingUnfitDisjointOrExcess(fitParent, unfitParent);
    }

    @Override
    public GenomeDefault crossOverByEqualTreatment(final GenomeDefault parent1, final GenomeDefault parent2) {
        return genomeCrossOver.crossOverByEqualTreatment(parent1, parent2);
    }
}
