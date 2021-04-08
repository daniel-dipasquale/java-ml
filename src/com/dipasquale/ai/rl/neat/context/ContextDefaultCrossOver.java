package com.dipasquale.ai.rl.neat.context;

import com.dipasquale.ai.common.GateProvider;
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
}
