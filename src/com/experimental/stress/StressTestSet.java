package com.experimental.stress;

import com.dipasquale.common.DateTimeSupport;
import com.google.common.collect.ImmutableList;
import lombok.Builder;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public final class StressTestSet extends StressTestStrategy {
    private StressTestSet(final StressTest stressTest) {
        super(stressTest);
    }

    private static <T> List<StressTest> createStressTests(final String name,
                                                          final Set<T> set,
                                                          final DataGenerator<T> dataGenerator,
                                                          final DateTimeSupport dateTimeSupport) {
        return ImmutableList.<StressTest>builder()
                .add(StressTestPredicate.<T>builder()
                        .serviceName(name)
                        .operationName("add")
                        .predicate(set::add)
                        .iterable(dataGenerator.getAddGenerator())
                        .dateTimeSupport(dateTimeSupport)
                        .build())
                .add(StressTestPredicate.<T>builder()
                        .serviceName(name)
                        .operationName("contains")
                        .predicate(set::contains)
                        .iterable(dataGenerator.getAddGenerator())
                        .dateTimeSupport(dateTimeSupport)
                        .build())
                .add(StressTestSupplier.<T, Integer>builder()
                        .serviceName(name)
                        .operationName("size")
                        .supplier(set::size)
                        .iterable(dataGenerator.getGetGenerator())
                        .dateTimeSupport(dateTimeSupport)
                        .build())
                .add(StressTestPredicate.<T>builder()
                        .serviceName(name)
                        .operationName("isEmpty")
                        .predicate(e -> set.isEmpty())
                        .iterable(dataGenerator.getGetGenerator())
                        .dateTimeSupport(dateTimeSupport)
                        .build())
                .add(StressTestPredicate.<T>builder()
                        .serviceName(name)
                        .operationName("remove")
                        .predicate(set::remove)
                        .iterable(dataGenerator.getRemoveGenerator())
                        .dateTimeSupport(dateTimeSupport)
                        .build())
                .build();
    }

    @Builder
    public static <T> StressTest create(final String name,
                                        final Set<T> set,
                                        final DataGenerator<T> dataGenerator,
                                        final DateTimeSupport dateTimeSupport,
                                        final ExecutorService executorService,
                                        final int repeat) {
        List<StressTest> stressTests = IntStream.range(0, repeat)
                .mapToObj(i -> createStressTests(name, set, dataGenerator, dateTimeSupport))
                .flatMap(List::stream).collect(Collectors.toList());

        StressTest stressTest = StressTestSequential.builder()
                .name(name)
                .stressTests(ImmutableList.<StressTest>builder()
                        .add(createStressTest(name, executorService, stressTests))
                        .add(StressTestRunnable.<T>builder()
                                .serviceName(name)
                                .operationName("clearForIterator")
                                .runnable(set::clear)
                                .iterable(dataGenerator.getIteratorGenerator())
                                .dateTimeSupport(dateTimeSupport)
                                .build())
                        .add(StressTestPredicate.<T>builder()
                                .serviceName(name)
                                .operationName("addForIterator")
                                .predicate(set::add)
                                .iterable(dataGenerator.getAddGenerator())
                                .dateTimeSupport(dateTimeSupport)
                                .build())
                        .add(StressTestIterator.<T>builder()
                                .serviceName(name)
                                .operationName("iterator")
                                .supplier(() -> set)
                                .iterable(dataGenerator.getIteratorGenerator())
                                .dateTimeSupport(dateTimeSupport)
                                .build())
                        .build())
                .stressTestNotification(StressTestNotification.EMPTY)
                .build();

        return new StressTestSet(stressTest);
    }
}
