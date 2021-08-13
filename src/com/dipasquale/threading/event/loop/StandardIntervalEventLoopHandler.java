/*
 * java-ml
 * (c) 2021 daniel-dipasquale
 * released under the MIT license
 */

package com.dipasquale.threading.event.loop;

import com.dipasquale.common.error.ErrorLogger;

import java.util.concurrent.CountDownLatch;

final class StandardIntervalEventLoopHandler implements EventLoopHandler {
    private final StandardEventLoopHandler standardHandler;
    private final IntervalEventLoopHandler underlyingHandler;
    private final ErrorLogger errorLogger;
    private final CountDownLatch invokedCountDownLatch;
    private final long delayTime;
    private final EventLoop nextEntryPoint;

    StandardIntervalEventLoopHandler(final IntervalEventLoopHandler handler, final long delayTime, final ErrorLogger errorLogger, final CountDownLatch invokedCountDownLatch, final EventLoop nextEntryPoint) {
        this.standardHandler = new StandardEventLoopHandler(handler, errorLogger, invokedCountDownLatch);
        this.underlyingHandler = handler;
        this.errorLogger = errorLogger;
        this.invokedCountDownLatch = invokedCountDownLatch;
        this.delayTime = delayTime;
        this.nextEntryPoint = nextEntryPoint;
    }

    @Override
    public void handle(final String name) {
        standardHandler.handle(name);

        if (underlyingHandler.shouldRequeue()) {
            nextEntryPoint.queue(underlyingHandler, delayTime, errorLogger, invokedCountDownLatch);
        }
    }
}
