package com.dipasquale.common;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

@FunctionalInterface
public interface RandomSupport {
    double next();

    default double next(final double min, final double max) {
        double value = next();

        return value * (max - min) + min;
    }

    default long next(final long min, final long max) {
        long maxFixed = max - 1L;

        if (min >= maxFixed) {
            return min;
        }

        double value = next();
        double minDouble = (double) min;
        double maxDouble = (double) maxFixed;

        return (long) (value * maxDouble - value * minDouble + value + minDouble);
    }

    default RandomSupport bounded(final double min, final double max) {
        return () -> next(min, max);
    }

    default boolean isBetween(final double min, final double max) {
        double value = next();

        return Double.compare(value, min) >= 0 && Double.compare(value, max) <= 0;
    }

    default boolean isAtMost(final double max) {
        return isBetween(0D, max);
    }

    static RandomSupport create() {
        return new Random()::nextDouble;
    }

    static RandomSupport createConcurrent() {
        return () -> ThreadLocalRandom.current().nextDouble();
    }

    private static RandomSupport createGaussian(final RandomSupport randomSupport) {
        double min = -5D;
        double max = 5D;

        return () -> {
            double value = randomSupport.next();
            double valueFixed = (value - min) * (max - min) / 100D;

            return Math.min(Math.max(valueFixed, 0D), 1D);
        };
    }

    static RandomSupport createGaussian() {
        return createGaussian(new Random()::nextGaussian);
    }

    static RandomSupport createGaussianConcurrent() {
        return createGaussian(() -> ThreadLocalRandom.current().nextGaussian());
    }
}
