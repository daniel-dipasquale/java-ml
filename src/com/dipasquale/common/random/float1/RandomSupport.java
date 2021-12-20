package com.dipasquale.common.random.float1;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

@FunctionalInterface
public interface RandomSupport {
    float next();

    default float next(final float min, final float max) {
        float value = next();

        return value * (max - min) + min;
    }

    default int next(final int min, final int max) {
        float value = next();
        float result = (float) Math.floor(value * (float) (max - min)) + min;

        return (int) result;
    }

    default RandomSupport bounded(final float min, final float max) {
        return new BoundedRandomSupport(this, min, max);
    }

    default boolean isBetween(final float min, final float max) {
        float value = next();

        return Float.compare(value, min) >= 0 && Float.compare(value, max) < 0;
    }

    default boolean isLessThan(final float max) {
        return isBetween(0f, max);
    }

    default <T> void shuffle(final List<T> items) {
        for (int i = items.size(); i > 1; i--) {
            int fromIndex = i - 1;
            int toIndex = next(0, i);
            T item = items.set(toIndex, items.get(fromIndex));

            items.set(fromIndex, item);
        }
    }

    default <T> List<T> shuffled(final List<T> items, final Class<T> itemType) {
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
