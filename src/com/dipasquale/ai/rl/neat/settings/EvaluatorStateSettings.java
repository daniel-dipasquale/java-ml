/*
 * java-ml
 * (c) 2021 daniel-dipasquale
 * released under the MIT license
 */

package com.dipasquale.ai.rl.neat.settings;

import com.dipasquale.ai.rl.neat.core.NeatEnvironment;
import com.dipasquale.threading.event.loop.IterableEventLoop;
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
    private final IterableEventLoop eventLoop;
    private final NeatEnvironment environment;
}
