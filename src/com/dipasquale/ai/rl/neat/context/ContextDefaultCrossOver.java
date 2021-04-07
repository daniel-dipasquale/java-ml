package com.dipasquale.ai.rl.neat.context;

import com.dipasquale.ai.common.GateProvider;
import com.dipasquale.ai.rl.neat.genotype.GenomeCrossOver;
import com.dipasquale.ai.rl.neat.genotype.GenomeDefault;
import lombok.RequiredArgsConstructor;

import java.io.Serial;

@RequiredArgsConstructor
public final class ContextDefaultCrossOver implements Context.CrossOver {
    @Serial
    private static final long serialVersionUID = -8201363182458911919L;
    private final GateProvider shouldMateAndMutate;
    private final GateProvider shouldMateOnly;
    private final GateProvider shouldMutateOnly;
    private final GateProvider shouldOverrideConnectionExpressed;
    private final GateProvider shouldUseRandomParentConnectionWeight;
    private final GenomeCrossOver genomeCrossOver;

    @Override
    public boolean shouldMateAndMutate() {
        return shouldMateAndMutate.isOn();
    }

    @Override
    public boolean shouldMateOnly() {
        return shouldMateOnly.isOn();
    }

    @Override
    public boolean shouldMutateOnly() {
        return shouldMutateOnly.isOn();
    }

    @Override
    public boolean shouldOverrideConnectionExpressed() {
        return shouldOverrideConnectionExpressed.isOn();
    }

    @Override
    public boolean shouldUseRandomParentConnectionWeight() {
        return shouldUseRandomParentConnectionWeight.isOn();
    }

    @Override
    public GenomeDefault crossOverBySkippingUnfitDisjointOrExcess(final Context context, final GenomeDefault fitParent, final GenomeDefault unfitParent) {
        return genomeCrossOver.crossOverBySkippingUnfitDisjointOrExcess(context, fitParent, unfitParent);
    }

    @Override
    public GenomeDefault crossOverByEqualTreatment(final Context context, final GenomeDefault parent1, final GenomeDefault parent2) {
        return genomeCrossOver.crossOverByEqualTreatment(context, parent1, parent2);
    }
}
