package com.dipasquale.threading;

interface IterableExecutorHandler<T> extends EventLoopHandler {
    T awaitReducedValue() throws InterruptedException;

    void stopProcessing();
}
