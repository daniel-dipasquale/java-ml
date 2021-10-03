package com.dipasquale.ai.rl.neat.settings;

import com.dipasquale.ai.rl.neat.common.RandomType;
import com.dipasquale.ai.rl.neat.context.DefaultContextMutationSupport;
import com.dipasquale.synchronization.dual.mode.random.float1.DualModeRandomSupport;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
public final class MutationSupport {
    @Builder.Default
    private final FloatNumber addNodeRate = FloatNumber.literal(0.03f);
    @Builder.Default
    private final FloatNumber addConnectionRate = FloatNumber.literal(0.06f);
    @Builder.Default
    private final FloatNumber perturbWeightRate = FloatNumber.literal(0.75f);
    @Builder.Default
    private final FloatNumber replaceWeightRate = FloatNumber.literal(0.5f);
    @Builder.Default
    private final FloatNumber disableExpressedConnectionRate = FloatNumber.literal(0.015f);

    DefaultContextMutationSupport create(final ParallelismSupport parallelismSupport, final DualModeRandomSupport randomSupport, final Map<RandomType, DualModeRandomSupport> randomSupports) {
        return DefaultContextMutationSupport.create(parallelismSupport, randomSupports, randomSupport, this);
    }
}
