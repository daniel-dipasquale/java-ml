package com.dipasquale.common;

import java.io.Serial;
import java.io.Serializable;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

@FunctionalInterface
public interface RandomSupport extends Serializable {
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

    private static RandomSupport create(final RandomSupport randomSupport) {
        return new RandomSupport() {
            @Serial
            private static final long serialVersionUID = 4254847174604671066L;

            @Override
            public double next() {
                return randomSupport.next();
            }
        };
    }

    static RandomSupport create(final boolean contended) {
        if (!contended) {
            return create(new Random()::nextDouble);
        }

        return create(() -> ThreadLocalRandom.current().nextDouble());
    }

    static RandomSupport createMeanDistribution(final boolean contended, final int concentration) {
        return new RandomSupportMeanDistribution(create(contended), concentration);
    }

    static RandomSupport createMeanDistribution(final boolean contended) {
        return createMeanDistribution(contended, 5);
    }
}
