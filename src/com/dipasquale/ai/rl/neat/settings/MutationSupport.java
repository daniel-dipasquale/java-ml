package com.dipasquale.ai.rl.neat.settings;

import com.dipasquale.ai.rl.neat.context.DefaultContextMutationSupport;
import com.dipasquale.common.random.float1.RandomSupport;
import com.dipasquale.synchronization.dual.profile.ObjectProfile;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

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

    DefaultContextMutationSupport create(final ParallelismSupport parallelismSupport, final ObjectProfile<RandomSupport> randomSupportProfile) {
        return DefaultContextMutationSupport.create(parallelismSupport, randomSupportProfile, this);
    }
}
