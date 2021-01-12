package com.experimental.stress;

import com.dipasquale.common.RandomSupport;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import lombok.Getter;

import java.util.AbstractMap;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.IntFunction;
import java.util.stream.IntStream;
import java.util.stream.StreamSupport;

public final class DataGeneratorFactory {
    private static Iterable<Integer> createNumbersInAscendingOrder(final int offset, final int count) {
        return IntStream.range(offset, offset + count)::iterator;
    }

    private static <T> Iterable<T> createObjectsInAscendingOrder(final int offset, final int count, final IntFunction<T> mapper) {
        return StreamSupport.stream(createNumbersInAscendingOrder(offset, count).spliterator(), false)
                .map(mapper::apply)
                ::iterator;
    }

    private static Iterable<Integer> createNumbersInRandomOrder(final int count) {
        List<Integer> numbers = Lists.newArrayList(IntStream.range(0, count)::iterator);

        Collections.shuffle(numbers);

        return numbers;
    }

    private static Iterable<Integer> createRandomNumbers(final int count, final RandomSupport randomSupport, final int minimum, final int maximum) {
        double difference = (double) maximum - (double) minimum;

        return IntStream.range(0, count)
                .map(i -> (int) (randomSupport.next() * difference) + minimum)
                ::iterator;
    }

    private static <T> Iterable<T> createRandomObjects(final int count, final RandomSupport randomSupport, final int minimum, final int maximum, final IntFunction<T> mapper) {
        return StreamSupport.stream(createRandomNumbers(count, randomSupport, minimum, maximum).spliterator(), false)
                .map(mapper::apply)
                ::iterator;
    }

    public static DataGenerator<Integer> createNumbersInAscendingOrder(final int offset, final int count, final int iteratorCount) {
        return new DataGenerator<Integer>() {
            @Getter
            private final Iterable<Integer> addGenerator = ImmutableList.copyOf(createNumbersInAscendingOrder(offset, count));
            @Getter
            private final Iterable<Integer> removeGenerator = ImmutableList.copyOf(createNumbersInAscendingOrder(offset, count));
            @Getter
            private final Iterable<Integer> getGenerator = ImmutableList.copyOf(createNumbersInAscendingOrder(offset, count));
            @Getter
            private final Iterable<Integer> iteratorGenerator = ImmutableList.copyOf(createNumbersInAscendingOrder(0, iteratorCount));
        };
    }

    public static DataGenerator<Integer> createNumbersInRandomOrder(final int count, final int iteratorCount) {
        return new DataGenerator<Integer>() {
            @Getter
            private final Iterable<Integer> addGenerator = ImmutableList.copyOf(createNumbersInRandomOrder(count));
            @Getter
            private final Iterable<Integer> removeGenerator = ImmutableList.copyOf(createNumbersInRandomOrder(count));
            @Getter
            private final Iterable<Integer> getGenerator = ImmutableList.copyOf(createNumbersInRandomOrder(count));
            @Getter
            private final Iterable<Integer> iteratorGenerator = ImmutableList.copyOf(createNumbersInAscendingOrder(0, iteratorCount));
        };
    }

    public static DataGenerator<Integer> createRandomNumbers(final int count, final int iteratorCount, final RandomSupport randomSupport, final int minimum, final int maximum) {
        return new DataGenerator<Integer>() {
            @Getter
            private final Iterable<Integer> addGenerator = ImmutableList.copyOf(createRandomNumbers(count, randomSupport, minimum, maximum));
            @Getter
            private final Iterable<Integer> removeGenerator = ImmutableList.copyOf(createRandomNumbers(count, randomSupport, minimum, maximum));
            @Getter
            private final Iterable<Integer> getGenerator = ImmutableList.copyOf(createRandomNumbers(count, randomSupport, minimum, maximum));
            @Getter
            private final Iterable<Integer> iteratorGenerator = ImmutableList.copyOf(createNumbersInAscendingOrder(0, iteratorCount));
        };
    }

    public static DataGenerator<Map.Entry<Integer, String>> createEntriesInAscendingOrder(final int offset, final int count, final int iteratorCount) {
        return new DataGenerator<Map.Entry<Integer, String>>() {
            @Getter
            private final Iterable<Map.Entry<Integer, String>> addGenerator = ImmutableList.copyOf(createObjectsInAscendingOrder(offset, count, i -> new AbstractMap.SimpleEntry<>(i, Integer.toString(i))));
            @Getter
            private final Iterable<Map.Entry<Integer, String>> removeGenerator = ImmutableList.copyOf(createObjectsInAscendingOrder(offset, count, i -> new AbstractMap.SimpleEntry<>(i, Integer.toString(i))));
            @Getter
            private final Iterable<Map.Entry<Integer, String>> getGenerator = ImmutableList.copyOf(createObjectsInAscendingOrder(offset, count, i -> new AbstractMap.SimpleEntry<>(i, Integer.toString(i))));
            @Getter
            private final Iterable<Map.Entry<Integer, String>> iteratorGenerator = ImmutableList.copyOf(createObjectsInAscendingOrder(0, iteratorCount, i -> new AbstractMap.SimpleEntry<>(i, Integer.toString(i))));
        };
    }

    public static DataGenerator<Map.Entry<Integer, String>> createRandomEntries(final int count, final int iteratorCount, final RandomSupport randomSupport, final int minimum, final int maximum) {
        return new DataGenerator<Map.Entry<Integer, String>>() {
            @Getter
            private final Iterable<Map.Entry<Integer, String>> addGenerator = ImmutableList.copyOf(createRandomObjects(count, randomSupport, minimum, maximum, i -> new AbstractMap.SimpleEntry<>(i, Integer.toString(i))));
            @Getter
            private final Iterable<Map.Entry<Integer, String>> removeGenerator = ImmutableList.copyOf(createRandomObjects(count, randomSupport, minimum, maximum, i -> new AbstractMap.SimpleEntry<>(i, Integer.toString(i))));
            @Getter
            private final Iterable<Map.Entry<Integer, String>> getGenerator = ImmutableList.copyOf(createRandomObjects(count, randomSupport, minimum, maximum, i -> new AbstractMap.SimpleEntry<>(i, Integer.toString(i))));
            @Getter
            private final Iterable<Map.Entry<Integer, String>> iteratorGenerator = ImmutableList.copyOf(createObjectsInAscendingOrder(0, iteratorCount, i -> new AbstractMap.SimpleEntry<>(i, Integer.toString(i))));
        };
    }
}
