package com.dipasquale.synchronization.event.loop;

import com.dipasquale.common.error.ErrorHandler;
import com.dipasquale.synchronization.wait.handle.InteractiveWaitHandle;

final class StandardIntervalEventLoopHandler implements EventLoopHandler {
    private final StandardEventLoopHandler standardHandler;
    private final IntervalEventLoopHandler underlyingHandler;
    private final ErrorHandler errorHandler;
    private final InteractiveWaitHandle invokedWaitHandle;
    private final long delayTime;
    private final EventLoop entryPoint;

    StandardIntervalEventLoopHandler(final IntervalEventLoopHandler handler, final long delayTime, final ErrorHandler errorHandler, final InteractiveWaitHandle invokedWaitHandle, final EventLoop entryPoint) {
        this.standardHandler = new StandardEventLoopHandler(handler, errorHandler, invokedWaitHandle);
        this.underlyingHandler = handler;
        this.errorHandler = errorHandler;
        this.invokedWaitHandle = invokedWaitHandle;
        this.delayTime = delayTime;
        this.entryPoint = entryPoint;
    }

    @Override
    public void handle() {
        standardHandler.handle();

        if (underlyingHandler.shouldRequeue()) {
            entryPoint.queue(underlyingHandler, delayTime, errorHandler, invokedWaitHandle);
        }
    }
}
