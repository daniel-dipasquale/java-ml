package com.dipasquale.ai.rl.neat;

import com.dipasquale.ai.rl.neat.generational.gate.GenerationalIsLessThanRandomGate;
import com.dipasquale.io.serialization.SerializableStateGroup;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class DefaultNeatContextCrossOverSupport implements NeatContext.CrossOverSupport {
    private final GenerationalIsLessThanRandomGate shouldOverrideExpressedConnectionGate;
    private final GenerationalIsLessThanRandomGate shouldUseWeightFromRandomParentGate;

    @Override
    public boolean shouldOverrideExpressedConnection() {
        return shouldOverrideExpressedConnectionGate.isOn();
    }

    @Override
    public boolean shouldUseWeightFromRandomParent() {
        return shouldUseWeightFromRandomParentGate.isOn();
    }

    @Override
    public void advanceGeneration() {
        shouldOverrideExpressedConnectionGate.reinitialize();
        shouldUseWeightFromRandomParentGate.reinitialize();
    }

    void save(final SerializableStateGroup stateGroup) {
        stateGroup.put("crossOver.shouldOverrideExpressedConnectionGate", shouldOverrideExpressedConnectionGate);
        stateGroup.put("crossOver.shouldUseWeightFromRandomParentGate", shouldUseWeightFromRandomParentGate);
    }

    static DefaultNeatContextCrossOverSupport create(final SerializableStateGroup stateGroup) {
        GenerationalIsLessThanRandomGate shouldOverrideExpressedConnectionGate = stateGroup.get("crossOver.shouldOverrideExpressedConnectionGate");
        GenerationalIsLessThanRandomGate shouldUseWeightFromRandomParentGate = stateGroup.get("crossOver.shouldUseWeightFromRandomParentGate");

        return new DefaultNeatContextCrossOverSupport(shouldOverrideExpressedConnectionGate, shouldUseWeightFromRandomParentGate);
    }
}
