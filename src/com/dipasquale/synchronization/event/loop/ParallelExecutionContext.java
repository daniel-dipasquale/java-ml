package com.dipasquale.synchronization.event.loop;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@Getter(AccessLevel.PACKAGE)
public final class ParallelExecutionContext<T> {
    private final List<EventLoopHandler> handlers = new ArrayList<>();
    @Setter(AccessLevel.PACKAGE)
    private T argument = null;
    private final Lock lock = new ReentrantLock();
}
