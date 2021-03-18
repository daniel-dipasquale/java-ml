package com.dipasquale.common;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

@FunctionalInterface
public interface RandomSupportFloat {
    float next();

    default float next(final float min, final float max) {
        float value = next();

        return value * (max - min) + min;
    }

    default int next(final int min, final int max) {
        int maxFixed = max - 1;

        if (min >= maxFixed) {
            return min;
        }

        float value = next();
        float minFloat = (float) min;
        float maxFloat = (float) maxFixed;

        return (int) (value * maxFloat - value * minFloat + value + minFloat);
    }

    default RandomSupportFloat bounded(final float min, final float max) {
        return () -> next(min, max);
    }

    default boolean isBetween(final float min, final float max) {
        float value = next();

        return Float.compare(value, min) >= 0 && Float.compare(value, max) < 0;
    }

    default boolean isLessThan(final float max) {
        return isBetween(0f, max);
    }

    static RandomSupportFloat create() {
        return new Random()::nextFloat;
    }

    static RandomSupportFloat createConcurrent() {
        return () -> ThreadLocalRandom.current().nextFloat();
    }
}
