package com.dipasquale.common.random.float1;

import com.dipasquale.data.structure.group.ListSetGroup;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

@FunctionalInterface
public interface RandomSupport {
    float next();

    default float next(final float minimum, final float maximum) {
        float value = next();

        return value * (maximum - minimum) + minimum;
    }

    default int next(final int minimum, final int maximum) {
        double value = next();

        return (int) Math.floor(value * (double) (maximum - minimum)) + minimum;
    }

    default RandomSupport bounded(final float minimum, final float maximum) {
        return new BoundedRandomSupport(this, minimum, maximum);
    }

    default boolean isBetween(final float minimum, final float maximum) {
        float value = next();

        return Float.compare(value, minimum) >= 0 && Float.compare(value, maximum) < 0;
    }

    default boolean isLessThan(final float maximum) {
        return isBetween(0f, maximum);
    }

    default <T> void shuffle(final List<T> items) {
        for (int i = items.size(); i > 1; i--) {
            int fromIndex = i - 1;
            int toIndex = next(0, i);
            T replacedItem = items.set(toIndex, items.get(fromIndex));

            items.set(fromIndex, replacedItem);
        }
    }

    default <T> void shuffle(final ListSetGroup<?, T> items) {
        for (int i = items.size(); i > 1; i--) {
            int fromIndex = i - 1;
            int toIndex = next(0, i);

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
                int toIndex = next(0, i);
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
