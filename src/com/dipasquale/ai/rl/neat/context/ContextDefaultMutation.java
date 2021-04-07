package com.dipasquale.ai.rl.neat.context;

import com.dipasquale.ai.common.GateProvider;
import lombok.RequiredArgsConstructor;

import java.io.Serial;

@RequiredArgsConstructor
public final class ContextDefaultMutation implements Context.Mutation {
    @Serial
    private static final long serialVersionUID = 5944299841392988551L;
    private final GateProvider shouldAddNodeMutation;
    private final GateProvider shouldAddConnectionMutation;
    private final GateProvider shouldPerturbConnectionWeight;
    private final GateProvider shouldReplaceConnectionWeight;
    private final GateProvider shouldDisableConnectionExpressed;

    @Override
    public boolean shouldAddNodeMutation() {
        return shouldAddNodeMutation.isOn();
    }

    @Override
    public boolean shouldAddConnectionMutation() {
        return shouldAddConnectionMutation.isOn();
    }

    @Override
    public boolean shouldPerturbConnectionWeight() {
        return shouldPerturbConnectionWeight.isOn();
    }

    @Override
    public boolean shouldReplaceConnectionWeight() {
        return shouldReplaceConnectionWeight.isOn();
    }

    @Override
    public boolean shouldDisableConnectionExpressed() {
        return shouldDisableConnectionExpressed.isOn();
    }
}
