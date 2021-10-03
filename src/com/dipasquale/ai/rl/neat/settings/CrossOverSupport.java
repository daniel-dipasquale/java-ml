package com.dipasquale.ai.rl.neat.settings;

import com.dipasquale.ai.rl.neat.common.RandomType;
import com.dipasquale.ai.rl.neat.context.DefaultContextCrossOverSupport;
import com.dipasquale.synchronization.dual.mode.random.float1.DualModeRandomSupport;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
public final class CrossOverSupport {
    @Builder.Default
    private final FloatNumber overrideExpressedConnectionRate = FloatNumber.literal(0.5f);
    @Builder.Default
    private final FloatNumber useWeightFromRandomParentRate = FloatNumber.literal(0.6f);

    DefaultContextCrossOverSupport create(final ParallelismSupport parallelismSupport, final DualModeRandomSupport randomSupport, final Map<RandomType, DualModeRandomSupport> randomSupports) {
        return DefaultContextCrossOverSupport.create(parallelismSupport, randomSupports, randomSupport, this);
    }
}
