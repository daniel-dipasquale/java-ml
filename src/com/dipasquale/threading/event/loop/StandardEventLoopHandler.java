/*
 * java-ml
 * (c) 2021 daniel-dipasquale
 * released under the MIT license
 */

package com.dipasquale.threading.event.loop;

import com.dipasquale.common.error.ErrorLogger;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.concurrent.CountDownLatch;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class StandardEventLoopHandler implements EventLoopHandler {
    private final EventLoopHandler underlyingHandler;
    private final ErrorLogger errorLogger;
    private final CountDownLatch invokedCountDownLatch;

    @Override
    public void handle(final String name) {
        try {
            underlyingHandler.handle(name);
        } catch (Throwable e) {
            if (errorLogger == null) {
                throw e;
            }

            errorLogger.log(e);
        } finally {
            if (invokedCountDownLatch != null) {
                invokedCountDownLatch.countDown();
            }
        }
    }
}
