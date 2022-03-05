package com.dipasquale.ai.rl.neat;

import com.dipasquale.io.serialization.SerializableStateGroup;
import com.dipasquale.synchronization.dual.mode.DualModeObject;
import com.dipasquale.synchronization.dual.mode.gate.DualModeIsLessThanRandomGate;
import com.dipasquale.synchronization.event.loop.BatchingEventLoop;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
final class ContextObjectCrossOverSupport implements Context.CrossOverSupport {
    private DualModeIsLessThanRandomGate shouldOverrideExpressedConnectionGate;
    private DualModeIsLessThanRandomGate shouldUseWeightFromRandomParentGate;

    private static DualModeIsLessThanRandomGate createIsLessThanGate(final InitializationContext initializationContext, final FloatNumber maximum) {
        return new DualModeIsLessThanRandomGate(initializationContext.createDefaultRandomSupport(), initializationContext.getFloatSingleton(maximum));
    }

    static ContextObjectCrossOverSupport create(final InitializationContext initializationContext, final CrossOverSupport crossOverSupport) {
        DualModeIsLessThanRandomGate shouldOverrideExpressedConnectionGate = createIsLessThanGate(initializationContext, crossOverSupport.getOverrideExpressedConnectionRate());
        DualModeIsLessThanRandomGate shouldUseWeightFromRandomParentGate = createIsLessThanGate(initializationContext, crossOverSupport.getUseWeightFromRandomParentRate());

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

    public void save(final SerializableStateGroup stateGroup) {
        stateGroup.put("crossOver.shouldOverrideExpressedConnectionGate", shouldOverrideExpressedConnectionGate);
        stateGroup.put("crossOver.shouldUseWeightFromRandomParentGate", shouldUseWeightFromRandomParentGate);
    }

    private void load(final SerializableStateGroup stateGroup, final int concurrencyLevel) {
        shouldOverrideExpressedConnectionGate = DualModeObject.activateMode(stateGroup.get("crossOver.shouldOverrideExpressedConnectionGate"), concurrencyLevel);
        shouldUseWeightFromRandomParentGate = DualModeObject.activateMode(stateGroup.get("crossOver.shouldUseWeightFromRandomParentGate"), concurrencyLevel);
    }

    public void load(final SerializableStateGroup stateGroup, final BatchingEventLoop eventLoop) {
        load(stateGroup, ParallelismSupport.getConcurrencyLevel(eventLoop));
    }
}
