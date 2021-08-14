package com.dipasquale.threading.event.loop;

import com.dipasquale.common.error.ErrorHandler;
import com.dipasquale.threading.wait.handle.InteractiveWaitHandle;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class StandardEventLoopHandler implements EventLoopHandler {
    private final EventLoopHandler handler;
    private final ErrorHandler errorHandler;
    private final InteractiveWaitHandle invokedWaitHandle;

    @Override
    public void handle(final String name) {
        if (invokedWaitHandle != null) {
            invokedWaitHandle.start();
        }

        try {
            handler.handle(name);
        } catch (Throwable e) {
            if (errorHandler == null || !errorHandler.handle(e)) {
                throw e;
            }
        } finally {
            if (invokedWaitHandle != null) {
                invokedWaitHandle.complete();
            }
        }
    }
}
