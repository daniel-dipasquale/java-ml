package com.dipasquale.ai.rl.neat;

import com.dipasquale.threading.event.loop.EventLoopIterable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter(AccessLevel.PACKAGE)
public final class SettingsEvaluatorState {
    private final boolean meantToLoadSettings;
    private final EventLoopIterable eventLoop;
    private final NeatEnvironment environment;
    private final boolean meantToLoadTopology;
}
