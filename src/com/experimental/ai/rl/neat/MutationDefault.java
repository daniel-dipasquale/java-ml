package com.experimental.ai.rl.neat;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class MutationDefault implements Context.Mutation {
    private final float addNodeMutationsRate;
    private final float addConnectionMutationsRate;
    private final float perturbConnectionWeightRate;
    private final float changeConnectionExpressedRate;

    @Override
    public float addNodeMutationsRate() {
        return addNodeMutationsRate;
    }

    @Override
    public float addConnectionMutationsRate() {
        return addConnectionMutationsRate;
    }

    @Override
    public float perturbConnectionWeightRate() {
        return perturbConnectionWeightRate;
    }

    @Override
    public float changeConnectionExpressedRate() {
        return changeConnectionExpressedRate;
    }
}
