package com.dipasquale.common.test;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class JvmWarmup {
    private void invoke() {
    }

    public static void start(final int count) {
        for (int i = 0; i < count; i++) {
            new JvmWarmup().invoke();
        }
    }
}