package com.dipasquale.threading.wait.handle;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.concurrent.TimeUnit;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
final class EmptyWaitHandle implements WaitHandle {
    private static final EmptyWaitHandle INSTANCE = new EmptyWaitHandle();

    public static EmptyWaitHandle getInstance() {
        return INSTANCE;
    }

    @Override
    public void await()
            throws InterruptedException {
    }

    @Override
    public boolean await(final long timeout, final TimeUnit unit)
            throws InterruptedException {
        return false;
    }
}
