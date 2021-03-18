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

        return Double.compare(value, min) >= 0 && Double.compare(value, max) < 0;
    }

    default boolean isLessThan(final double max) {
        return isBetween(0D, max);
    }

    static RandomSupport create() {
        return new Random()::nextDouble;
    }

    static RandomSupport createConcurrent() {
        return () -> ThreadLocalRandom.current().nextDouble();
    }

    private static RandomSupport createGaussian(final RandomSupport randomSupport, final double max) {
        double min = -max;
        double maxFixed = Math.nextDown(1D);

        return () -> {
            double value = randomSupport.next();
            double valueFixed = (value - min) * (max - min) / 100D;

            return Math.min(Math.max(valueFixed, 0D), maxFixed);
        };
    }

    static RandomSupport createGaussian() {
        return createGaussian(new Random()::nextGaussian, 5D);
    }

    static RandomSupport createGaussianConcurrent() {
        return createGaussian(() -> ThreadLocalRandom.current().nextGaussian(), 5D);
    }

    static RandomSupport createGaussianUnbounded() {
        return new Random()::nextGaussian;
    }

    static RandomSupport createGaussianConcurrentUnbounded() {
        return () -> ThreadLocalRandom.current().nextGaussian();
    }
}
