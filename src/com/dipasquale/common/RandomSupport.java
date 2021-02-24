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

    private static RandomSupport createGaussian(final RandomSupport randomSupport, final double limit) {
        double limitFixed = Math.abs(limit);
        double min = -limitFixed;
        double max = limitFixed;

        return () -> {
            double value = randomSupport.next();
            double valueFixed = (value - min) * (max - min) / 100D;

            return Math.min(Math.max(valueFixed, 0D), 1D);
        };
    }

    static RandomSupport createGaussian(final double limit) {
        return createGaussian(new Random()::nextGaussian, limit);
    }

    static RandomSupport createGaussian() {
        return createGaussian(5D);
    }

    static RandomSupport createGaussianUnbounded() {
        return new Random()::nextGaussian;
    }

    static RandomSupport createGaussianConcurrent(final double limit) {
        return createGaussian(() -> ThreadLocalRandom.current().nextGaussian(), limit);
    }

    static RandomSupport createGaussianConcurrent() {
        return createGaussianConcurrent(5D);
    }

    static RandomSupport createGaussianConcurrentUnbounded() {
        return () -> ThreadLocalRandom.current().nextGaussian();
    }
}
