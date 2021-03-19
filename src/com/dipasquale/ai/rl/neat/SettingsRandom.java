package com.dipasquale.ai.rl.neat;

import com.dipasquale.ai.rl.neat.context.ContextDefaultComponentFactory;
import com.dipasquale.ai.rl.neat.context.ContextDefaultRandom;
import com.dipasquale.common.RandomSupportFloat;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class SettingsRandom {
    @Builder.Default
    private final RandomSupportFloat randomSupport = RandomSupportFloat.createConcurrent();

    ContextDefaultComponentFactory<ContextDefaultRandom> createFactory() {
        return context -> new ContextDefaultRandom(randomSupport, randomSupport, randomSupport);
    }
}
