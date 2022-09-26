package com.dipasquale.ai.rl.neat;

import com.dipasquale.ai.rl.neat.generational.factory.GenerationalWeightMutationTypeFactory;
import com.dipasquale.ai.rl.neat.generational.gate.GenerationalIsLessThanRandomGate;
import com.dipasquale.io.serialization.SerializableStateGroup;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class DefaultNeatContextMutationSupport implements NeatContext.MutationSupport {
    private final GenerationalIsLessThanRandomGate shouldAddNodeGate;
    private final GenerationalIsLessThanRandomGate shouldAddConnectionGate;
    private final GenerationalWeightMutationTypeFactory weightMutationTypeFactory;
    private final GenerationalIsLessThanRandomGate shouldDisableExpressedConnectionGate;

    @Override
    public boolean shouldAddNode() {
        return shouldAddNodeGate.isOn();
    }

    @Override
    public boolean shouldAddConnection() {
        return shouldAddConnectionGate.isOn();
    }

    @Override
    public WeightMutationType generateWeightMutationType() {
        return weightMutationTypeFactory.create();
    }

    @Override
    public boolean shouldDisableExpressedConnection() {
        return shouldDisableExpressedConnectionGate.isOn();
    }

    @Override
    public void advanceGeneration() {
        shouldAddConnectionGate.reinitialize();
        shouldAddConnectionGate.reinitialize();
        weightMutationTypeFactory.reinitialize();
        shouldDisableExpressedConnectionGate.reinitialize();
    }

    void save(final SerializableStateGroup stateGroup) {
        stateGroup.put("mutation.shouldAddNodeGate", shouldAddNodeGate);
        stateGroup.put("mutation.shouldAddConnectionGate", shouldAddConnectionGate);
        stateGroup.put("mutation.weightMutationTypeFactory", weightMutationTypeFactory);
        stateGroup.put("mutation.shouldDisableExpressedConnectionGate", shouldDisableExpressedConnectionGate);
    }

    static DefaultNeatContextMutationSupport create(final SerializableStateGroup stateGroup) {
        GenerationalIsLessThanRandomGate shouldAddNodeGate = stateGroup.get("mutation.shouldAddNodeGate");
        GenerationalIsLessThanRandomGate shouldAddConnectionGate = stateGroup.get("mutation.shouldAddConnectionGate");
        GenerationalWeightMutationTypeFactory weightMutationTypeFactory = stateGroup.get("mutation.weightMutationTypeFactory");
        GenerationalIsLessThanRandomGate shouldDisableExpressedConnectionGate = stateGroup.get("mutation.shouldDisableExpressedConnectionGate");

        return new DefaultNeatContextMutationSupport(shouldAddNodeGate, shouldAddConnectionGate, weightMutationTypeFactory, shouldDisableExpressedConnectionGate);
    }
}
