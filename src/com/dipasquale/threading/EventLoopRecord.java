package com.dipasquale.threading;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@Getter
final class EventLoopRecord {
    private final Runnable handler;
    private final long executionDateTime;
}
