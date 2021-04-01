package com.dipasquale.ai.rl.neat;

import com.dipasquale.ai.rl.neat.context.ContextDefaultComponentFactory;
import com.dipasquale.ai.rl.neat.context.ContextDefaultRandom;
import com.dipasquale.common.RandomSupportFloat;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public final class SettingsRandom {
    @Builder.Default
    private final RandomSupportFloat nextIndexRandomSupport = SettingsConstants.RANDOM_SUPPORT_UNIFORM;
    @Builder.Default
    final RandomSupportFloat isLessThanRandomSupport = SettingsConstants.RANDOM_SUPPORT_UNIFORM;

    ContextDefaultComponentFactory<ContextDefaultRandom> createFactory() {
        return context -> new ContextDefaultRandom(nextIndexRandomSupport, isLessThanRandomSupport);
    }
}
