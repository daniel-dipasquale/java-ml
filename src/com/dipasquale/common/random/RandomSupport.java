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

    default <T> void shuffle(final List<T> elements) {
        for (int i = elements.size(); i > 1; i--) {
            int fromIndex = i - 1;
            int toIndex = nextInteger(0, i);
            T replacedElement = elements.set(toIndex, elements.get(fromIndex));

            elements.set(fromIndex, replacedElement);
        }
    }

    default <T> void shuffle(final ListSetGroup<?, T> elements) {
        for (int i = elements.size(); i > 1; i--) {
            int fromIndex = i - 1;
            int toIndex = nextInteger(0, i);

            elements.swap(fromIndex, toIndex);
        }
    }

    default <T> List<T> createShuffled(final List<T> elements, final Class<T> elementType) {
        int size = elements.size();
        T[] shuffledElements = (T[]) Array.newInstance(elementType, size);

        if (size > 0) {
            shuffledElements[0] = elements.get(0);

            for (int i = elements.size(); i > 1; i--) {
                int fromIndex = i - 1;
                int toIndex = nextInteger(0, i);
                T element = shuffledElements[toIndex];

                if (element != null) {
                    shuffledElements[fromIndex] = element;
                }

                shuffledElements[toIndex] = elements.get(fromIndex);
            }
        }

        return Arrays.asList(shuffledElements); // TODO: create a more friendly type of ArrayList for this
    }
}
