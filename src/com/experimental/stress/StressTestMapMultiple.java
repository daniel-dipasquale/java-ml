package com.experimental.stress;

import com.dipasquale.common.time.DateTimeSupport;
import lombok.Builder;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

public final class StressTestMapMultiple extends StressTestStrategy {
    private StressTestMapMultiple(final StressTest stressTest) {
        super(stressTest);
    }

    @Builder
    public static <TKey, TValue> StressTest create(final String name,
                                                   final ExecutorService executorService,
                                                   final List<Map<TKey, TValue>> maps,
                                                   final DataGenerator<Map.Entry<TKey, TValue>> dataGenerator,
                                                   final DateTimeSupport dateTimeSupport,
                                                   final int repeat,
                                                   final StressTestNotification stressTestNotification) {
        StressTest stressTest = StressTestParallel.builder()
                .name(name)
                .executorService(executorService)
                .stressTests(maps.stream()
                        .map(m -> StressTestMap.<TKey, TValue>builder()
                                .name(m.getClass().getSimpleName())
                                .map(m)
                                .dataGenerator(dataGenerator)
                                .dateTimeSupport(dateTimeSupport)
                                .executorService(repeat > 1 ? executorService : null)
                                .repeat(repeat)
                                .build())
                        .collect(Collectors.toList()))
                .stressTestNotification(stressTestNotification)
                .build();

        return new StressTestMapMultiple(stressTest);
    }
}
