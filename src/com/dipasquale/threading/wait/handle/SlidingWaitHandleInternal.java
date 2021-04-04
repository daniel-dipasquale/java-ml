package com.dipasquale.threading.wait.handle;

import java.util.concurrent.TimeUnit;

interface SlidingWaitHandleInternal {
    void await() throws InterruptedException;

    boolean await(long timeout, TimeUnit unit) throws InterruptedException;

    void changeAwait(long timeout, TimeUnit unit);

    void release();
}