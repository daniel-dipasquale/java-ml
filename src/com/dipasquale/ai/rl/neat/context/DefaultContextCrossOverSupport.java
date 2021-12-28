package com.dipasquale.ai.rl.neat.context;

import com.dipasquale.ai.rl.neat.core.CrossOverSupport;
import com.dipasquale.ai.rl.neat.core.FloatNumber;
import com.dipasquale.ai.rl.neat.core.InitializationContext;
import com.dipasquale.ai.rl.neat.core.ParallelismSupport;
import com.dipasquale.io.serialization.SerializableStateGroup;
import com.dipasquale.synchronization.dual.mode.DualModeObject;
import com.dipasquale.synchronization.dual.mode.gate.DualModeIsLessThanRandomGate;
import com.dipasquale.synchronization.event.loop.IterableEventLoop;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class DefaultContextCrossOverSupport implements Context.CrossOverSupport {
    private DualModeIsLessThanRandomGate shouldOverrideExpressedConnectionGate;
    private DualModeIsLessThanRandomGate shouldUseWeightFromRandomParentGate;

    private static DualModeIsLessThanRandomGate createIsLessThanGate(final InitializationContext initializationContext, final FloatNumber max) {
        return new DualModeIsLessThanRandomGate(initializationContext.createDefaultRandomSupport(), max.getSingletonValue(initializationContext));
    }

    public static DefaultContextCrossOverSupport create(final InitializationContext initializationContext, final CrossOverSupport crossOverSupport) {
        DualModeIsLessThanRandomGate shouldOverrideExpressedConnectionGate = createIsLessThanGate(initializationContext, crossOverSupport.getOverrideExpressedConnectionRate());
        DualModeIsLessThanRandomGate shouldUseWeightFromRandomParentGate = createIsLessThanGate(initializationContext, crossOverSupport.getUseWeightFromRandomParentRate());

        return new DefaultContextCrossOverSupport(shouldOverrideExpressedConnectionGate, shouldUseWeightFromRandomParentGate);
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

    public void load(final SerializableStateGroup stateGroup, final IterableEventLoop eventLoop) {
        shouldOverrideExpressedConnectionGate = DualModeObject.activateMode(stateGroup.get("crossOver.shouldOverrideExpressedConnectionGate"), ParallelismSupport.getConcurrencyLevel(eventLoop));
        shouldUseWeightFromRandomParentGate = DualModeObject.activateMode(stateGroup.get("crossOver.shouldUseWeightFromRandomParentGate"), ParallelismSupport.getConcurrencyLevel(eventLoop));
    }
}
