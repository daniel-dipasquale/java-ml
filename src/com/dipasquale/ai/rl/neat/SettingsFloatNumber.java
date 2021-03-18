package com.dipasquale.ai.rl.neat;

import com.dipasquale.common.RandomSupportFloat;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class SettingsFloatNumber {
    private static final RandomSupportFloat RANDOM_SUPPORT = RandomSupportFloat.createConcurrent();
    private static final RandomSupportFloat RANDOM_SUPPORT_MEAN_DISTRIBUTION = RandomSupportFloat.createMeanDistributionConcurrent();
    private final Supplier supplier;

    public static SettingsFloatNumber literal(final float number) {
        return new SettingsFloatNumber(() -> number);
    }

    public static SettingsFloatNumber random(final float min, final float max) {
        return new SettingsFloatNumber(() -> RANDOM_SUPPORT.next(min, max));
    }

    public static SettingsFloatNumber randomMeanDistribution(final float min, final float max) {
        return new SettingsFloatNumber(() -> RANDOM_SUPPORT_MEAN_DISTRIBUTION.next(min, max));
    }

    public static SettingsFloatNumber strategy(final Supplier supplier) {
        return new SettingsFloatNumber(supplier);
    }

    float get() {
        return supplier.get();
    }

    @FunctionalInterface
    public interface Supplier {
        float get();
    }
}
