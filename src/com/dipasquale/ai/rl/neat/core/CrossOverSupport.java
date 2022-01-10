package com.dipasquale.ai.rl.neat.core;

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

    ContextObjectCrossOverSupport create(final InitializationContext initializationContext) {
        return ContextObjectCrossOverSupport.create(initializationContext, this);
    }
}
