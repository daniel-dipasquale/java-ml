package com.dipasquale.ai.rl.neat;

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

    ContextObjectRandomSupport create(final InitializationContext initializationContext) {
        return ContextObjectRandomSupport.create(initializationContext);
    }
}
