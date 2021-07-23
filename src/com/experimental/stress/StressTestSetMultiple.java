package com.experimental.stress;

import com.dipasquale.common.time.DateTimeSupport;
import lombok.Builder;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

public final class StressTestSetMultiple extends StressTestStrategy {
    private StressTestSetMultiple(final StressTest stressTest) {
        super(stressTest);
    }

    @Builder
    public static <T> StressTest create(final String name,
                                        final ExecutorService executorService,
                                        final List<Set<T>> sets,
                                        final DataGenerator<T> dataGenerator,
                                        final DateTimeSupport dateTimeSupport,
                                        final int repeat,
                                        final StressTestNotification stressTestNotification) {
        StressTest stressTest = StressTestParallel.builder()
                .name(name)
                .executorService(executorService)
                .stressTests(sets.stream()
                        .map(s -> StressTestSet.<T>builder()
                                .name(s.getClass().getSimpleName())
                                .set(s)
                                .dataGenerator(dataGenerator)
                                .dateTimeSupport(dateTimeSupport)
                                .executorService(repeat > 1 ? executorService : null)
                                .repeat(repeat)
                                .build())
                        .collect(Collectors.toList()))
                .stressTestNotification(stressTestNotification)
                .build();

        return new StressTestSetMultiple(stressTest);
    }
}
