package com.dipasquale.common.random.float2;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

@FunctionalInterface
public interface RandomSupport {
    double next();

    default double next(final double min, final double max) {
        double value = next();

        return value * (max - min) + min;
    }

    default long next(final long min, final long max) {
        double value = next();

        return (long) Math.floor(value * (double) (max - min)) + min;
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

    default <T> void shuffle(final List<T> items) {
        for (int i = items.size(); i > 1; i--) {
            int fromIndex = i - 1;
            int toIndex = (int) next(0L, i);
            T item = items.set(toIndex, items.get(fromIndex));

            items.set(fromIndex, item);
        }
    }

    default <T> List<T> createShuffled(final List<T> items, final Class<T> itemType) {
        int size = items.size();
        T[] shuffledItems = (T[]) Array.newInstance(itemType, size);

        if (size > 0) {
            shuffledItems[0] = items.get(0);

            for (int i = size; i > 1; i--) {
                int fromIndex = i - 1;
                int toIndex = (int) next(0L, i);
                T item = shuffledItems[toIndex];

                if (item != null) {
                    shuffledItems[fromIndex] = item;
                }

                shuffledItems[toIndex] = items.get(fromIndex);
            }
        }

        return Arrays.asList(shuffledItems);
    }
}
