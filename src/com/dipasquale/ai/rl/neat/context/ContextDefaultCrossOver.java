package com.dipasquale.ai.rl.neat.context;

import com.dipasquale.ai.rl.neat.genotype.GenomeCrossOver;
import com.dipasquale.ai.rl.neat.genotype.GenomeDefault;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class ContextDefaultCrossOver implements Context.CrossOver {
    private final Supplier shouldMateAndMutate;
    private final Supplier shouldMateOnly;
    private final Supplier shouldMutateOnly;
    private final Supplier shouldOverrideConnectionExpressed;
    private final Supplier shouldUseRandomParentConnectionWeight;
    private final GenomeCrossOver genomeCrossOver;

    @Override
    public boolean shouldMateAndMutate() {
        return shouldMateAndMutate.get();
    }

    @Override
    public boolean shouldMateOnly() {
        return shouldMateOnly.get();
    }

    @Override
    public boolean shouldMutateOnly() {
        return shouldMutateOnly.get();
    }

    @Override
    public boolean shouldOverrideConnectionExpressed() {
        return shouldOverrideConnectionExpressed.get();
    }

    @Override
    public boolean shouldUseRandomParentConnectionWeight() {
        return shouldUseRandomParentConnectionWeight.get();
    }

    @Override
    public GenomeDefault crossOverBySkippingUnfitDisjointOrExcess(final GenomeDefault fitParent, final GenomeDefault unfitParent) {
        return genomeCrossOver.crossOverBySkippingUnfitDisjointOrExcess(fitParent, unfitParent);
    }

    @Override
    public GenomeDefault crossOverByEqualTreatment(final GenomeDefault parent1, final GenomeDefault parent2) {
        return genomeCrossOver.crossOverByEqualTreatment(parent1, parent2);
    }

    @FunctionalInterface
    public interface Supplier {
        boolean get();
    }
}
