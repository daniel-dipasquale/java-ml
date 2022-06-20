package com.dipasquale.synchronization.event.loop;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class RangeEventLoopHandler implements EventLoopHandler {
    private final RangeHandler rangeHandler;
    private final int offset;
    private final int count;

    @Override
    public void handle(final EventLoopId id) {
        for (int i = offset, c = offset + count; i < c; i++) {
            if (!rangeHandler.handle(id, i)) {
                return;
            }
        }
    }
}
