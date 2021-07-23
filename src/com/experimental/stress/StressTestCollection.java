package com.experimental.stress;

import com.dipasquale.common.time.DateTimeSupport;
import com.google.common.collect.ImmutableList;
import lombok.Builder;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public final class StressTestCollection extends StressTestStrategy {
    private StressTestCollection(final StressTest stressTest) {
        super(stressTest);
    }

    private static <T> List<StressTest> createStressTests(final String name,
                                                          final Collection<T> collection,
                                                          final DataGenerator<T> dataGenerator,
                                                          final DateTimeSupport dateTimeSupport) {
        return ImmutableList.<StressTest>builder()
                .add(StressTestConsumer.<T>builder()
                        .serviceName(name)
                        .operationName("add")
                        .consumer(collection::add)
                        .iterable(dataGenerator.getAddGenerator())
                        .dateTimeSupport(dateTimeSupport)
                        .build())
                .add(StressTestSupplier.<T, Integer>builder()
                        .serviceName(name)
                        .operationName("size")
                        .supplier(collection::size)
                        .iterable(dataGenerator.getGetGenerator())
                        .dateTimeSupport(dateTimeSupport)
                        .build())
                .add(StressTestPredicate.<T>builder()
                        .serviceName(name)
                        .operationName("isEmpty")
                        .predicate(e -> collection.isEmpty())
                        .iterable(dataGenerator.getGetGenerator())
                        .dateTimeSupport(dateTimeSupport)
                        .build())
                .add(StressTestPredicate.<T>builder()
                        .serviceName(name)
                        .operationName("remove")
                        .predicate(collection::remove)
                        .iterable(dataGenerator.getRemoveGenerator())
                        .dateTimeSupport(dateTimeSupport)
                        .build())
                .build();
    }

    @Builder
    public static <T> StressTest create(final String name,
                                        final Collection<T> collection,
                                        final DataGenerator<T> dataGenerator,
                                        final DateTimeSupport dateTimeSupport,
                                        final ExecutorService executorService,
                                        final int repeat) {
        List<StressTest> stressTests = IntStream.range(0, repeat)
                .mapToObj(i -> createStressTests(name, collection, dataGenerator, dateTimeSupport))
                .flatMap(List::stream).collect(Collectors.toList());

        StressTest stressTest = StressTestSequential.builder()
                .name(name)
                .stressTests(ImmutableList.<StressTest>builder()
                        .add(createStressTest(name, executorService, stressTests))
                        .add(StressTestRunnable.<T>builder()
                                .serviceName(name)
                                .operationName("clearForIterator")
                                .runnable(collection::clear)
                                .iterable(dataGenerator.getAddGenerator())
                                .dateTimeSupport(dateTimeSupport)
                                .build())
                        .add(StressTestConsumer.<T>builder()
                                .serviceName(name)
                                .operationName("addForIterator")
                                .consumer(collection::add)
                                .iterable(dataGenerator.getAddGenerator())
                                .dateTimeSupport(dateTimeSupport)
                                .build())
                        .add(StressTestIterator.<T>builder()
                                .serviceName(name)
                                .operationName("iterator")
                                .supplier(() -> collection)
                                .iterable(dataGenerator.getIteratorGenerator())
                                .dateTimeSupport(dateTimeSupport)
                                .build())
                        .build())
                .stressTestNotification(StressTestNotification.EMPTY)
                .build();

        return new StressTestCollection(stressTest);
    }
}
