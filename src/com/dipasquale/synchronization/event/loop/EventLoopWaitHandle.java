package com.dipasquale.synchronization.event.loop;

import com.dipasquale.synchronization.wait.handle.WaitHandle;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class EventLoopWaitHandle implements WaitHandle {
    private final EventLoop eventLoop;

    @Override
    public void await()
            throws InterruptedException {
        eventLoop.awaitUntilEmpty();
    }

    @Override
    public boolean await(final long timeout, final TimeUnit unit)
            throws InterruptedException {
        return eventLoop.awaitUntilEmpty(timeout, unit);
    }
}
