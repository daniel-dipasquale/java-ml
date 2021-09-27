package com.dipasquale.ai.rl.neat.settings;

import com.dipasquale.ai.rl.neat.common.RandomType;
import com.dipasquale.ai.rl.neat.context.DefaultContextRandomSupport;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
public final class RandomSupport {
    @Builder.Default
    private final RandomType type = RandomType.UNIFORM;

    DefaultContextRandomSupport create(final ParallelismSupport parallelismSupport) {
        return DefaultContextRandomSupport.create(parallelismSupport, this);
    }
}
