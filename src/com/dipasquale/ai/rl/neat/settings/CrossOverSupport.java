package com.dipasquale.ai.rl.neat.settings;

import com.dipasquale.ai.rl.neat.context.DefaultContextCrossOverSupport;
import com.dipasquale.common.random.float1.RandomSupport;
import com.dipasquale.synchronization.dual.profile.ObjectProfile;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
public final class CrossOverSupport {
    @Builder.Default
    private final FloatNumber overrideExpressedConnectionRate = FloatNumber.literal(0.5f);
    @Builder.Default
    private final FloatNumber useWeightFromRandomParentRate = FloatNumber.literal(0.6f);

    DefaultContextCrossOverSupport create(final ParallelismSupport parallelismSupport, final ObjectProfile<RandomSupport> randomSupportProfile) {
        return DefaultContextCrossOverSupport.create(parallelismSupport, randomSupportProfile, this);
    }
}
