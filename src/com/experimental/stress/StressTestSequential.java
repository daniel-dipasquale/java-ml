package com.experimental.stress;

import com.experimental.metrics.MetricDataCollector;
import com.experimental.metrics.MetricDataSelector;
import com.google.common.collect.ImmutableList;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.stream.StreamSupport;

@Builder
public final class StressTestSequential implements StressTest {
    @Getter
    private final String name;
    private final List<StressTest> stressTests;
    private final StressTestNotification stressTestNotification;

    @Override
    public void perform(final MetricDataCollector metricDataCollector) {
        for (StressTest stressTest : stressTests) {
            stressTestNotification.notifyStarting(stressTest);
            stressTest.perform(metricDataCollector);
            stressTestNotification.notifyCompleted(stressTest);
        }
    }

    @Override
    public Iterable<MetricDataSelector> extractMetricDataSelectors() {
        return ImmutableList.<MetricDataSelector>builder()
                .addAll(stressTests.stream()
                        .flatMap(st -> StreamSupport.stream(st.extractMetricDataSelectors().spliterator(), false))
                        ::iterator)
                .build();
    }

    @Override
    public Iterable<Throwable> extractExceptions() {
        return stressTests.stream()
                .flatMap(st -> StreamSupport.stream(st.extractExceptions().spliterator(), false))
                ::iterator;
    }
}
