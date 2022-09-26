package com.dipasquale.ai.rl.neat;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
public final class RandomnessSettings {
    @Builder.Default
    private final RandomType type = RandomType.UNIFORM;

    DefaultNeatContextRandomnessSupport create(final NeatInitializationContext initializationContext) {
        return DefaultNeatContextRandomnessSupport.create(initializationContext);
    }
}
