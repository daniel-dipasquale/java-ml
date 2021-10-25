package com.dipasquale.ai.rl.neat.context;

import com.dipasquale.ai.rl.neat.core.CrossOverSupport;
import com.dipasquale.ai.rl.neat.core.FloatNumber;
import com.dipasquale.ai.rl.neat.core.InitializationContext;
import com.dipasquale.ai.rl.neat.core.ParallelismSupport;
import com.dipasquale.io.serialization.SerializableStateGroup;
import com.dipasquale.synchronization.dual.mode.DualModeObject;
import com.dipasquale.synchronization.dual.mode.provider.DualModeIsLessThanRandomGateProvider;
import com.dipasquale.synchronization.event.loop.IterableEventLoop;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class DefaultContextCrossOverSupport implements Context.CrossOverSupport {
    private DualModeIsLessThanRandomGateProvider shouldOverrideExpressedConnectionGateProvider;
    private DualModeIsLessThanRandomGateProvider shouldUseWeightFromRandomParentGateProvider;

    private static DualModeIsLessThanRandomGateProvider createIsLessThanGateProvider(final InitializationContext initializationContext, final FloatNumber max) {
        return new DualModeIsLessThanRandomGateProvider(initializationContext.getRandomSupport(), max.getSingletonValue(initializationContext));
    }

    public static DefaultContextCrossOverSupport create(final InitializationContext initializationContext, final CrossOverSupport crossOverSupport) {
        DualModeIsLessThanRandomGateProvider shouldOverrideExpressedConnectionGateProvider = createIsLessThanGateProvider(initializationContext, crossOverSupport.getOverrideExpressedConnectionRate());
        DualModeIsLessThanRandomGateProvider shouldUseWeightFromRandomParentGateProvider = createIsLessThanGateProvider(initializationContext, crossOverSupport.getUseWeightFromRandomParentRate());

        return new DefaultContextCrossOverSupport(shouldOverrideExpressedConnectionGateProvider, shouldUseWeightFromRandomParentGateProvider);
    }

    @Override
    public boolean shouldOverrideExpressedConnection() {
        return shouldOverrideExpressedConnectionGateProvider.isOn();
    }

    @Override
    public boolean shouldUseWeightFromRandomParent() {
        return shouldUseWeightFromRandomParentGateProvider.isOn();
    }

    public void save(final SerializableStateGroup stateGroup) {
        stateGroup.put("crossOver.shouldOverrideExpressedConnectionGateProvider", shouldOverrideExpressedConnectionGateProvider);
        stateGroup.put("crossOver.shouldUseWeightFromRandomParentGateProvider", shouldUseWeightFromRandomParentGateProvider);
    }

    public void load(final SerializableStateGroup stateGroup, final IterableEventLoop eventLoop) {
        shouldOverrideExpressedConnectionGateProvider = DualModeObject.activateMode(stateGroup.get("crossOver.shouldOverrideExpressedConnectionGateProvider"), ParallelismSupport.getConcurrencyLevel(eventLoop));
        shouldUseWeightFromRandomParentGateProvider = DualModeObject.activateMode(stateGroup.get("crossOver.shouldUseWeightFromRandomParentGateProvider"), ParallelismSupport.getConcurrencyLevel(eventLoop));
    }
}
