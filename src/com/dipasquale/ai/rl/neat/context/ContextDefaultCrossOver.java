package com.dipasquale.ai.rl.neat.context;

import com.dipasquale.ai.rl.neat.genotype.GenomeCrossOver;
import com.dipasquale.ai.rl.neat.genotype.GenomeDefault;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class ContextDefaultCrossOver implements Context.CrossOver {
    private final float rate;
    private final float overrideExpressedRate;
    private final float useRandomParentWeightRate;
    private final GenomeCrossOver genomeCrossOver;

    @Override
    public float rate() {
        return rate;
    }

    @Override
    public float overrideExpressedRate() {
        return overrideExpressedRate;
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
