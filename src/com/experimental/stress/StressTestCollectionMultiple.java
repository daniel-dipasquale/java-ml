package com.experimental.stress;

import com.dipasquale.common.DateTimeSupport;
import lombok.Builder;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

public final class StressTestCollectionMultiple extends StressTestStrategy {
    private StressTestCollectionMultiple(final StressTest stressTest) {
        super(stressTest);
    }

    @Builder
    public static <T> StressTest create(final String name,
                                        final ExecutorService executorService,
                                        final List<Collection<T>> collections,
                                        final DataGenerator<T> dataGenerator,
                                        final DateTimeSupport dateTimeSupport,
                                        final int repeat,
                                        final StressTestNotification stressTestNotification) {
        StressTest stressTest = StressTestParallel.builder()
                .name(name)
                .executorService(executorService)
                .stressTests(collections.stream()
                        .map(c -> StressTestCollection.<T>builder()
                                .name(c.getClass().getSimpleName())
                                .collection(c)
                                .dataGenerator(dataGenerator)
                                .dateTimeSupport(dateTimeSupport)
                                .executorService(repeat > 1 ? executorService : null)
                                .repeat(repeat)
                                .build())
                        .collect(Collectors.toList()))
                .stressTestNotification(stressTestNotification)
                .build();

        return new StressTestCollectionMultiple(stressTest);
    }
}
