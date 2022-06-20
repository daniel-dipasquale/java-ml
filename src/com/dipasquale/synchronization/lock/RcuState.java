package com.dipasquale.synchronization.lock;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@Getter
final class RcuState {
    private final boolean isReading;
    private final boolean isWriting;
    private final Object token;
}
