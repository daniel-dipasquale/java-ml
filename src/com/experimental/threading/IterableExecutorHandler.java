package com.experimental.threading;

import com.dipasquale.threading.event.loop.EventLoopHandler;

interface IterableExecutorHandler<T> extends EventLoopHandler {
    T awaitReducedValue() throws InterruptedException;

    void stopProcessing();
}
