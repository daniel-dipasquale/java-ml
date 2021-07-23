package com.dipasquale.threading.event.loop;

import com.dipasquale.common.error.ErrorLogger;

import java.util.concurrent.CountDownLatch;

final class EventLoopIntervalHandlerProxy extends EventLoopHandlerProxy {
    private final EventLoopIntervalHandler handler;
    private final long delayTime;
    private final EventLoop nextEventLoop;

    EventLoopIntervalHandlerProxy(final EventLoopIntervalHandler handler, final long delayTime, final ErrorLogger errorLogger, final CountDownLatch countDownLatch, final EventLoop nextEventLoop) {
        super(handler, errorLogger, countDownLatch);
        this.handler = handler;
        this.delayTime = delayTime;
        this.nextEventLoop = nextEventLoop;
    }

    @Override
    public void handle(final String name) {
        super.handle(name);

        if (handler.shouldRequeue()) {
            nextEventLoop.queue(handler, delayTime, errorLogger, countDownLatch);
        }
    }
}
