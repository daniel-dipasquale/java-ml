package com.experimental.stress;

import com.dipasquale.common.DateTimeSupport;
import com.google.common.collect.ImmutableList;
import lombok.Builder;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public final class StressTestMap extends StressTestStrategy {
    private StressTestMap(final StressTest stressTest) {
        super(stressTest);
    }

    private static <TKey, TValue> List<StressTest> createStressTests(final String name,
                                                                     final Map<TKey, TValue> map,
                                                                     final DataGenerator<Map.Entry<TKey, TValue>> dataGenerator,
                                                                     final DateTimeSupport dateTimeSupport) {
        return ImmutableList.<StressTest>builder()
                .add(StressTestPredicate.<Map.Entry<TKey, TValue>>builder()
                        .serviceName(name)
                        .operationName("put")
                        .predicate(e -> map.put(e.getKey(), e.getValue()) == null)
                        .iterable(dataGenerator.getAddGenerator())
                        .dateTimeSupport(dateTimeSupport)
                        .build())
                .add(StressTestPredicate.<Map.Entry<TKey, TValue>>builder()
                        .serviceName(name)
                        .operationName("get")
                        .predicate(e -> map.get(e.getKey()) != null)
                        .iterable(dataGenerator.getGetGenerator())
                        .dateTimeSupport(dateTimeSupport)
                        .build())
                .add(StressTestSupplier.<Map.Entry<TKey, TValue>, Integer>builder()
                        .serviceName(name)
                        .operationName("size")
                        .supplier(map::size)
                        .iterable(dataGenerator.getGetGenerator())
                        .dateTimeSupport(dateTimeSupport)
                        .build())
                .add(StressTestPredicate.<Map.Entry<TKey, TValue>>builder()
                        .serviceName(name)
                        .operationName("isEmpty")
                        .predicate(e -> map.isEmpty())
                        .iterable(dataGenerator.getGetGenerator())
                        .dateTimeSupport(dateTimeSupport)
                        .build())
                .add(StressTestPredicate.<Map.Entry<TKey, TValue>>builder()
                        .serviceName(name)
                        .operationName("remove")
                        .predicate(e -> map.remove(e.getKey()) != null)
                        .iterable(dataGenerator.getRemoveGenerator())
                        .dateTimeSupport(dateTimeSupport)
                        .build())
                .build();
    }

    @Builder
    private static <TKey, TValue> StressTest create(final String name,
                                                    final Map<TKey, TValue> map,
                                                    final DataGenerator<Map.Entry<TKey, TValue>> dataGenerator,
                                                    final DateTimeSupport dateTimeSupport,
                                                    final ExecutorService executorService,
                                                    final int repeat) {
        List<StressTest> stressTests = IntStream.range(0, repeat)
                .mapToObj(i -> createStressTests(name, map, dataGenerator, dateTimeSupport))
                .flatMap(List::stream).collect(Collectors.toList());

        StressTest stressTest = StressTestSequential.builder()
                .name(name)
                .stressTests(ImmutableList.<StressTest>builder()
                        .add(createStressTest(name, executorService, stressTests))
                        .add(StressTestRunnable.<Map.Entry<TKey, TValue>>builder()
                                .serviceName(name)
                                .operationName("clearForIterator")
                                .runnable(map::clear)
                                .iterable(dataGenerator.getAddGenerator())
                                .dateTimeSupport(dateTimeSupport)
                                .build())
                        .add(StressTestPredicate.<Map.Entry<TKey, TValue>>builder()
                                .serviceName(name)
                                .operationName("putForIterator")
                                .predicate(e -> map.put(e.getKey(), e.getValue()) == null)
                                .iterable(dataGenerator.getAddGenerator())
                                .dateTimeSupport(dateTimeSupport)
                                .build())
                        .add(StressTestIterator.<Map.Entry<TKey, TValue>>builder()
                                .serviceName(name)
                                .operationName("iterator")
                                .supplier(map::entrySet)
                                .iterable(dataGenerator.getIteratorGenerator())
                                .dateTimeSupport(dateTimeSupport)
                                .build())
                        .build())
                .stressTestNotification(StressTestNotification.EMPTY)
                .build();

        return new StressTestMap(stressTest);
    }
}
