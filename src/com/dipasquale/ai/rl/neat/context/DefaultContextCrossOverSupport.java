package com.dipasquale.ai.rl.neat.context;

import com.dipasquale.ai.rl.neat.common.RandomType;
import com.dipasquale.ai.rl.neat.settings.CrossOverSupport;
import com.dipasquale.ai.rl.neat.settings.FloatNumber;
import com.dipasquale.ai.rl.neat.settings.ParallelismSupport;
import com.dipasquale.common.serialization.SerializableStateGroup;
import com.dipasquale.synchronization.dual.mode.DualModeObject;
import com.dipasquale.synchronization.dual.mode.provider.DualModeIsLessThanRandomGateProvider;
import com.dipasquale.synchronization.dual.mode.random.float1.DualModeRandomSupport;
import com.dipasquale.synchronization.event.loop.IterableEventLoop;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

import java.util.Map;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class DefaultContextCrossOverSupport implements Context.CrossOverSupport {
    private DualModeIsLessThanRandomGateProvider shouldOverrideExpressedConnectionGateProvider;
    private DualModeIsLessThanRandomGateProvider shouldUseWeightFromRandomParentGateProvider;

    private static DualModeIsLessThanRandomGateProvider createIsLessThanGateProvider(final ParallelismSupport parallelismSupport, final Map<RandomType, DualModeRandomSupport> randomSupports, final DualModeRandomSupport randomSupport, final FloatNumber max) {
        return new DualModeIsLessThanRandomGateProvider(randomSupport, max.getSingletonValue(parallelismSupport, randomSupports));
    }

    public static DefaultContextCrossOverSupport create(final ParallelismSupport parallelismSupport, final Map<RandomType, DualModeRandomSupport> randomSupports, final DualModeRandomSupport randomSupport, final CrossOverSupport crossOverSupport) {
        DualModeIsLessThanRandomGateProvider shouldOverrideExpressedConnectionGateProvider = createIsLessThanGateProvider(parallelismSupport, randomSupports, randomSupport, crossOverSupport.getOverrideExpressedConnectionRate());
        DualModeIsLessThanRandomGateProvider shouldUseWeightFromRandomParentGateProvider = createIsLessThanGateProvider(parallelismSupport, randomSupports, randomSupport, crossOverSupport.getUseWeightFromRandomParentRate());

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
