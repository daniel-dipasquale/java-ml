package com.dipasquale.ai.rl.neat.core;

import com.dipasquale.ai.rl.neat.context.DefaultContextMutationSupport;
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

    DefaultContextMutationSupport create(final InitializationContext initializationContext) {
        return DefaultContextMutationSupport.create(initializationContext, this);
    }
}
