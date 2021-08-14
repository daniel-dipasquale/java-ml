package com.dipasquale.threading.event.loop;

import com.dipasquale.common.error.ErrorHandler;
import com.dipasquale.threading.wait.handle.InteractiveWaitHandle;

final class StandardIntervalEventLoopHandler implements EventLoopHandler {
    private final StandardEventLoopHandler standardHandler;
    private final IntervalEventLoopHandler underlyingHandler;
    private final ErrorHandler errorHandler;
    private final InteractiveWaitHandle invokedWaitHandle;
    private final long delayTime;
    private final EventLoop nextEntryPoint;

    StandardIntervalEventLoopHandler(final IntervalEventLoopHandler handler, final long delayTime, final ErrorHandler errorHandler, final InteractiveWaitHandle invokedWaitHandle, final EventLoop nextEntryPoint) {
        this.standardHandler = new StandardEventLoopHandler(handler, errorHandler, invokedWaitHandle);
        this.underlyingHandler = handler;
        this.errorHandler = errorHandler;
        this.invokedWaitHandle = invokedWaitHandle;
        this.delayTime = delayTime;
        this.nextEntryPoint = nextEntryPoint;
    }

    @Override
    public void handle(final String name) {
        standardHandler.handle(name);

        if (underlyingHandler.shouldRequeue()) {
            nextEntryPoint.queue(underlyingHandler, delayTime, errorHandler, invokedWaitHandle);
        }
    }
}
