package com.dipasquale.threading.wait.handle;

import java.util.concurrent.TimeUnit;

interface InternalSlidingWaitHandle extends WaitHandle {
    void changeTimeout(long timeout, TimeUnit unit);

    void release();
}