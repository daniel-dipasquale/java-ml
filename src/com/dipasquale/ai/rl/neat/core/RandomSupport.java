package com.dipasquale.ai.rl.neat.core;

import com.dipasquale.ai.rl.neat.context.DefaultContextRandomSupport;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
public final class RandomSupport {
    @Builder.Default
    private final RandomType type = RandomType.UNIFORM;

    DefaultContextRandomSupport create(final InitializationContext initializationContext) {
        return DefaultContextRandomSupport.create(initializationContext);
    }
}
