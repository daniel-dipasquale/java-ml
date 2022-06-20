package com.dipasquale.synchronization.wait.handle;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum WaitCondition {
    ON_NOT_ZERO,
    ON_ZERO
}
