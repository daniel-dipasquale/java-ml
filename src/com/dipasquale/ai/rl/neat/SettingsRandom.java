package com.dipasquale.ai.rl.neat;

import com.dipasquale.common.RandomSupportFloat;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class SettingsRandom {
    @Builder.Default
    private final RandomSupportFloat randomSupport = RandomSupportFloat.createConcurrent();

    <T extends Comparable<T>> ContextDefaultComponentFactory<T, ContextDefaultRandom> createFactory() {
        return c -> new ContextDefaultRandom(randomSupport, randomSupport, randomSupport);
    }
}
