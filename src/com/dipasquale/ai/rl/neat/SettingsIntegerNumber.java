package com.dipasquale.ai.rl.neat;

import com.dipasquale.common.RandomSupport;
import com.dipasquale.common.RandomSupportFloat;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class SettingsIntegerNumber {
    private static final RandomSupportFloat RANDOM_SUPPORT = RandomSupportFloat.createConcurrent();
    private static final RandomSupport RANDOM_SUPPORT_GAUSSIAN = RandomSupport.createGaussianConcurrent();
    private final Supplier supplier;

    public static SettingsIntegerNumber literal(final int number) {
        return new SettingsIntegerNumber(() -> number);
    }

    public static SettingsIntegerNumber random(final int min, final int max) {
        return new SettingsIntegerNumber(() -> RANDOM_SUPPORT.next(min, max));
    }

    public static SettingsIntegerNumber randomGaussian(final int min, final int max) {
        return new SettingsIntegerNumber(() -> (int) RANDOM_SUPPORT_GAUSSIAN.next(min, max));
    }

    int get() {
        return supplier.get();
    }

    @FunctionalInterface
    private interface Supplier {
        int get();
    }
}
