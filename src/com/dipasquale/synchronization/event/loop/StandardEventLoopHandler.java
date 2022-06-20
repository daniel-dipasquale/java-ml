package com.dipasquale.synchronization.event.loop;

import com.dipasquale.common.error.ErrorHandler;
import com.dipasquale.synchronization.wait.handle.InteractiveWaitHandle;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class StandardEventLoopHandler implements EventLoopHandler {
    private final EventLoopHandler handler;
    private final ErrorHandler errorHandler;
    private final InteractiveWaitHandle invokedWaitHandle;

    @Override
    public void handle(final EventLoopId id) {
        if (invokedWaitHandle != null) {
            invokedWaitHandle.countUp();
        }

        try {
            handler.handle(id);
        } catch (Throwable e) {
            if (errorHandler == null || !errorHandler.handle(e)) {
                throw e;
            }
        } finally {
            if (invokedWaitHandle != null) {
                invokedWaitHandle.countDown();
            }
        }
    }
}
