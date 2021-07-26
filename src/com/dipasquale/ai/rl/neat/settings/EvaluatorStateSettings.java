package com.dipasquale.ai.rl.neat.settings;

import com.dipasquale.ai.rl.neat.core.NeatEnvironment;
import com.dipasquale.threading.event.loop.EventLoopIterable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter // TODO: consider fixing this in some other way
public final class EvaluatorStateSettings {
    private final boolean meantToOverrideTopology;
    private final boolean meantToOverrideSettings;
    private final EventLoopIterable eventLoop;
    private final NeatEnvironment environment;
}
