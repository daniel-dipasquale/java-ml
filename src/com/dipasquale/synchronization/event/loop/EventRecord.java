package com.dipasquale.synchronization.event.loop;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@Getter
final class EventRecord {
    private final EventLoopHandler handler;
    private final long executionDateTime;
}
