package com.dipasquale.ai.rl.neat.context;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class ContextDefaultMutation implements Context.Mutation {
    private final Supplier shouldAddNodeMutation;
    private final Supplier shouldAddConnectionMutation;
    private final Supplier shouldPerturbConnectionWeight;
    private final Supplier shouldReplaceConnectionWeight;
    private final Supplier shouldDisableConnectionExpressed;

    @Override
    public boolean shouldAddNodeMutation() {
        return shouldAddNodeMutation.get();
    }

    @Override
    public boolean shouldAddConnectionMutation() {
        return shouldAddConnectionMutation.get();
    }

    @Override
    public boolean shouldPerturbConnectionWeight() {
        return shouldPerturbConnectionWeight.get();
    }

    @Override
    public boolean shouldReplaceConnectionWeight() {
        return shouldReplaceConnectionWeight.get();
    }

    @Override
    public boolean shouldDisableConnectionExpressed() {
        return shouldDisableConnectionExpressed.get();
    }

    @FunctionalInterface
    public interface Supplier {
        boolean get();
    }
}
