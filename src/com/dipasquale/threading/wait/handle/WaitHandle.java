package com.dipasquale.threading.wait.handle;

import java.util.concurrent.TimeUnit;

public interface WaitHandle {
    void await() throws InterruptedException;

    boolean await(long timeout, TimeUnit unit) throws InterruptedException;

    static WaitHandle getThreadSleeper() {
        return ThreadSleeperWaitHandle.getInstance();
    }
}
