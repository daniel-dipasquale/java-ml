package com.dipasquale.threading.event.loop;

import com.dipasquale.threading.wait.handle.WaitHandle;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

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

    static List<EventLoopWaitHandle> translate(final List<EventLoop> eventLoops) {
        return eventLoops.stream()
                .map(EventLoopWaitHandle::new)
                .collect(Collectors.toList());
    }
}
