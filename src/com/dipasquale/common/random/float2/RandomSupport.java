package com.dipasquale.common.random.float2;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

@FunctionalInterface
public interface RandomSupport {
    double next();

    default double next(final double minimum, final double maximum) {
        double value = next();

        return value * (maximum - minimum) + minimum;
    }

    default long next(final long minimum, final long maximum) {
        double value = next();

        return (long) Math.floor(value * (double) (maximum - minimum)) + minimum;
    }

    default RandomSupport bounded(final double minimum, final double maximum) {
        return new BoundedRandomSupport(this, minimum, maximum);
    }

    default boolean isBetween(final double minimum, final double maximum) {
        double value = next();

        return Double.compare(value, minimum) >= 0 && Double.compare(value, maximum) < 0;
    }

    default boolean isLessThan(final double maximum) {
        return isBetween(0D, maximum);
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
