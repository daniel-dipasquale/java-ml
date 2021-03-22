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
        return new SettingsIntegerNumber(() -> RandomConstants.UNIFORM_CONCURRENT.next(min, max));
    }

    public static SettingsIntegerNumber randomMeanDistribution(final int min, final int max) {
        return new SettingsIntegerNumber(() -> RandomConstants.MEAN_DISTRIBUTED_CONCURRENT.next(min, max));
    }

    int get() {
        return supplier.get();
    }

    @FunctionalInterface
    private interface Supplier {
        int get();
    }
}
