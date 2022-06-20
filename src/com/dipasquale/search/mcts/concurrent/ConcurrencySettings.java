package com.dipasquale.search.mcts.concurrent;

import com.dipasquale.synchronization.event.loop.ParallelEventLoop;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Builder
@Getter
public final class ConcurrencySettings {
    private final ParallelEventLoop eventLoop;
    private final EdgeTraversalLockType edgeTraversalLockType;

    public static boolean isValid(final ConcurrencySettings concurrencySettings) {
        return concurrencySettings != null && concurrencySettings.eventLoop != null;
    }
}
