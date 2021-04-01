package com.dipasquale.ai.rl.neat;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class SettingsIntegerNumber {
    private final Supplier supplier;

    public static SettingsIntegerNumber literal(final int number) {
        return new SettingsIntegerNumber(() -> number);
    }

    public static SettingsIntegerNumber random(final int min, final int max) {
        return new SettingsIntegerNumber(() -> SettingsConstants.RANDOM_SUPPORT_UNIFORM.next(min, max));
    }

    public static SettingsIntegerNumber randomMeanDistribution(final int min, final int max) {
        return new SettingsIntegerNumber(() -> SettingsConstants.RANDOM_SUPPORT_MEAN_DISTRIBUTED.next(min, max));
    }

    int get() {
        return supplier.get();
    }

    @FunctionalInterface
    private interface Supplier {
        int get();
    }
}
