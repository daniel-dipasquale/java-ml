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

    private static RandomSupport createMeanDistribution(final RandomSupport randomSupport, final int concentration) {
        double multiplier = 1D / (double) concentration; // clever idea from: https://stackoverflow.com/questions/30492259/get-a-random-number-focused-on-center

        return () -> {
            double random = 0D;

            for (int i = 0; i < concentration; i++) {
                random += randomSupport.next() * multiplier;
            }

            return random;
        };
    }

    static RandomSupport createMeanDistribution(final int concentration) {
        return createMeanDistribution(new Random()::nextDouble, concentration);
    }

    static RandomSupport createMeanDistribution() {
        return createMeanDistribution(5);
    }

    static RandomSupport createMeanDistributionConcurrent(final int concentration) {
        return createMeanDistribution(() -> ThreadLocalRandom.current().nextDouble(), concentration);
    }

    static RandomSupport createMeanDistributionConcurrent() {
        return createMeanDistribution(5);
    }
}
