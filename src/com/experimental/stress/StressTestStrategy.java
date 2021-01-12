package com.experimental.stress;

import com.experimental.metrics.MetricDataCollector;
import com.experimental.metrics.MetricDataSelector;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.concurrent.ExecutorService;

@RequiredArgsConstructor
public class StressTestStrategy implements StressTest {
    private final StressTest stressTest;

    protected static StressTest createStressTest(final String name, final ExecutorService executorService, final List<StressTest> stressTests) {
        if (executorService == null) {
            return StressTestSequential.builder()
                    .name(name)
                    .stressTests(stressTests)
                    .stressTestNotification(StressTestNotification.EMPTY)
                    .build();
        }

        return StressTestParallel.builder()
                .name(name)
                .executorService(executorService)
                .stressTests(stressTests)
                .stressTestNotification(StressTestNotification.EMPTY)
                .build();
    }

    @Override
    public String getName() {
        return stressTest.getName();
    }

    @Override
    public void perform(final MetricDataCollector metricDataCollector) {
        stressTest.perform(metricDataCollector);
    }

    @Override
    public Iterable<MetricDataSelector> extractMetricDataSelectors() {
        return stressTest.extractMetricDataSelectors();
    }

    @Override
    public Iterable<Throwable> extractExceptions() {
        return stressTest.extractExceptions();
    }
}
