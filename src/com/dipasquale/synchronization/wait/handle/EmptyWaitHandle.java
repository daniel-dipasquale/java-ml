package com.dipasquale.synchronization.wait.handle;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
final class EmptyWaitHandle implements WaitHandle {
    private static final EmptyWaitHandle INSTANCE = new EmptyWaitHandle();

    public static EmptyWaitHandle getInstance() {
        return INSTANCE;
    }

    @Override
    public void await() {
    }

    @Override
    public boolean await(final long timeout, final TimeUnit unit) {
        return false;
    }
}
