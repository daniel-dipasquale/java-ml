package com.dipasquale.threading.event.loop;

import com.dipasquale.common.error.ErrorLogger;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.concurrent.CountDownLatch;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
class EventLoopHandlerProxy implements EventLoopHandler {
    private final EventLoopHandler handler;
    protected final ErrorLogger errorLogger;
    protected final CountDownLatch countDownLatch;

    @Override
    public void handle(final String name) {
        try {
            handler.handle(name);
        } catch (Throwable e) {
            if (errorLogger == null) {
                throw e;
            }

            errorLogger.log(e);
        } finally {
            if (countDownLatch != null) {
                countDownLatch.countDown();
            }
        }
    }
}
