package com.dipasquale.common.random;

import com.dipasquale.data.structure.group.ListSetGroup;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

public interface RandomSupport {
    float nextFloat();

    double nextDouble();

    default float nextFloat(final float minimum, final float maximum) {
        float value = nextFloat();

        return value * (maximum - minimum) + minimum;
    }

    default RandomSupport bounded(final float minimum, final float maximum) {
        return new BoundedRandomSupport(this, minimum, maximum);
    }

    default int nextInteger(final int minimum, final int maximum) {
        double value = nextFloat();

        return (int) Math.floor(value * (double) (maximum - minimum)) + minimum;
    }

    default double nextDouble(final double minimum, final double maximum) {
        double value = nextDouble();

        return value * (maximum - minimum) + minimum;
    }

    default RandomSupport bounded(final double minimum, final double maximum) {
        return new BoundedRandomSupport(this, minimum, maximum);
    }

    default long nextLong(final long minimum, final long maximum) {
        double value = nextDouble();

        return (long) Math.floor(value * (double) (maximum - minimum)) + minimum;
    }

    default boolean isBetween(final float minimum, final float maximum) {
        float value = nextFloat();

        return Float.compare(value, minimum) >= 0 && Float.compare(value, maximum) < 0;
    }

    default boolean isBetween(final double minimum, final double maximum) {
        double value = nextDouble();

        return Double.compare(value, minimum) >= 0 && Double.compare(value, maximum) < 0;
    }

    default boolean isLessThan(final float maximum) {
        return isBetween(0f, maximum);
    }

    default boolean isLessThan(final double maximum) {
        return isBetween(0D, maximum);
    }

    default <T> void shuffle(final List<T> items) {
        for (int i = items.size(); i > 1; i--) {
            int fromIndex = i - 1;
            int toIndex = nextInteger(0, i);
            T replacedItem = items.set(toIndex, items.get(fromIndex));

            items.set(fromIndex, replacedItem);
        }
    }

    default <T> void shuffle(final ListSetGroup<?, T> items) {
        for (int i = items.size(); i > 1; i--) {
            int fromIndex = i - 1;
            int toIndex = nextInteger(0, i);

            items.swap(fromIndex, toIndex);
        }
    }

    default <T> List<T> createShuffled(final List<T> items, final Class<T> itemType) {
        int size = items.size();
        T[] shuffledItems = (T[]) Array.newInstance(itemType, size);

        if (size > 0) {
            shuffledItems[0] = items.get(0);

            for (int i = items.size(); i > 1; i--) {
                int fromIndex = i - 1;
                int toIndex = nextInteger(0, i);
                T item = shuffledItems[toIndex];

                if (item != null) {
                    shuffledItems[fromIndex] = item;
                }

                shuffledItems[toIndex] = items.get(fromIndex);
            }
        }

        return Arrays.asList(shuffledItems); // TODO: create a more friendly type of ArrayList for this
    }
}
