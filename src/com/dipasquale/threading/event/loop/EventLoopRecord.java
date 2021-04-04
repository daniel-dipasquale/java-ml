package com.dipasquale.threading.event.loop;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@Getter
final class EventLoopRecord {
    private final EventLoopHandler handler;
    private final long executionDateTime;
}
