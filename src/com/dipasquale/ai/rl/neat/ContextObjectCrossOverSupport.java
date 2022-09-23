package com.dipasquale.ai.rl.neat;

import com.dipasquale.common.gate.IsLessThanRandomGate;
import com.dipasquale.io.serialization.SerializableStateGroup;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
final class ContextObjectCrossOverSupport implements Context.CrossOverSupport {
    private final IsLessThanRandomGate shouldOverrideExpressedConnectionGate;
    private final IsLessThanRandomGate shouldUseWeightFromRandomParentGate;

    private static IsLessThanRandomGate createIsLessThanGate(final InitializationContext initializationContext, final FloatNumber maximum) {
        return new IsLessThanRandomGate(initializationContext.createDefaultRandomSupport(), initializationContext.provideSingleton(maximum));
    }

    static ContextObjectCrossOverSupport create(final InitializationContext initializationContext, final CrossOverSettings crossOverSettings) {
        IsLessThanRandomGate shouldOverrideExpressedConnectionGate = createIsLessThanGate(initializationContext, crossOverSettings.getOverrideExpressedConnectionRate());
        IsLessThanRandomGate shouldUseWeightFromRandomParentGate = createIsLessThanGate(initializationContext, crossOverSettings.getUseWeightFromRandomParentRate());

        return new ContextObjectCrossOverSupport(shouldOverrideExpressedConnectionGate, shouldUseWeightFromRandomParentGate);
    }

    @Override
    public boolean shouldOverrideExpressedConnection() {
        return shouldOverrideExpressedConnectionGate.isOn();
    }

    @Override
    public boolean shouldUseWeightFromRandomParent() {
        return shouldUseWeightFromRandomParentGate.isOn();
    }

    void save(final SerializableStateGroup stateGroup) {
        stateGroup.put("crossOver.shouldOverrideExpressedConnectionGate", shouldOverrideExpressedConnectionGate);
        stateGroup.put("crossOver.shouldUseWeightFromRandomParentGate", shouldUseWeightFromRandomParentGate);
    }

    static ContextObjectCrossOverSupport create(final SerializableStateGroup stateGroup) {
        IsLessThanRandomGate shouldOverrideExpressedConnectionGate = stateGroup.get("crossOver.shouldOverrideExpressedConnectionGate");
        IsLessThanRandomGate shouldUseWeightFromRandomParentGate = stateGroup.get("crossOver.shouldUseWeightFromRandomParentGate");

        return new ContextObjectCrossOverSupport(shouldOverrideExpressedConnectionGate, shouldUseWeightFromRandomParentGate);
    }
}
