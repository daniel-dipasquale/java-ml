/*
 * java-ml
 * (c) 2021 daniel-dipasquale
 * released under the MIT license
 */

package com.dipasquale.common.random.float2;

@FunctionalInterface
public interface RandomSupport {
    double next();

    default double next(final double min, final double max) {
        double value = next();

        return value * (max - min) + min;
    }

    default long next(final long min, final long max) {
        long maxStep1 = max - 1L;
        double value = next();
        double minFixed = (float) min;
        double maxFixed = (float) maxStep1;

        return (long) (value * maxFixed - value * minFixed + value + minFixed);
    }

    default RandomSupport bounded(final double min, final double max) {
        return new BoundedRandomSupport(this, min, max);
    }

    default boolean isBetween(final double min, final double max) {
        double value = next();

        return Double.compare(value, min) >= 0 && Double.compare(value, max) < 0;
    }

    default boolean isLessThan(final double max) {
        return isBetween(0D, max);
    }
}
