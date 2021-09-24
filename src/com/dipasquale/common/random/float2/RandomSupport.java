package com.dipasquale.common.random.float2;

@FunctionalInterface
public interface RandomSupport {
    double next();

    default double next(final double min, final double max) {
        double value = next();

        return value * (max - min) + min;
    }

    default long next(final long min, final long max) {
        double value = next();
        double minFixed = (float) min;
        double maxFixed = (float) (max - 1L);
        double result = value * maxFixed - value * minFixed + value + minFixed;

        return (long) Math.min(result, maxFixed);
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
