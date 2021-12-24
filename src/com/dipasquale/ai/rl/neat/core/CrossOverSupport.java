package com.dipasquale.ai.rl.neat.core;

import com.dipasquale.ai.rl.neat.context.DefaultContextCrossOverSupport;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
public final class CrossOverSupport {
    @Builder.Default
    private final FloatNumber overrideExpressedConnectionRate = FloatNumber.literal(0.5f);
    @Builder.Default
    private final FloatNumber useWeightFromRandomParentRate = FloatNumber.literal(0.6f);

    DefaultContextCrossOverSupport create(final InitializationContext initializationContext) {
        return DefaultContextCrossOverSupport.create(initializationContext, this);
    }
}
