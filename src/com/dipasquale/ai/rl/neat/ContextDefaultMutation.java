package com.dipasquale.ai.rl.neat;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class ContextDefaultMutation implements Context.Mutation {
    private final float addNodeMutationsRate;
    private final float addConnectionMutationsRate;
    private final float perturbConnectionWeightRate;
    private final float replaceConnectionWeightRate;
    private final float disableConnectionExpressedRate;

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
    public float replaceConnectionWeightRate() {
        return replaceConnectionWeightRate;
    }

    @Override
    public float disableConnectionExpressedRate() {
        return disableConnectionExpressedRate;
    }
}
