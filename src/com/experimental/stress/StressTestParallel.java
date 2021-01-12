package com.experimental.stress;

import com.experimental.metrics.MetricDataCollector;
import com.experimental.metrics.MetricDataSelector;
import com.experimental.metrics.MetricKey;
import com.experimental.metrics.MetricKeyDefault;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import lombok.Builder;
import lombok.Getter;

import javax.measure.unit.Unit;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.stream.StreamSupport;

@Builder
public final class StressTestParallel implements StressTest {
    @Getter
    private final String name;
    private final ExecutorService executorService;
    private final List<StressTest> stressTests;
    private final StressTestNotification stressTestNotification;
    private final Queue<Throwable> exceptions = new LinkedList<>();

    @Override
    public void perform(final MetricDataCollector metricDataCollector) {
        String metricNameInterruptedFailure = String.format("%s.interrupted.failure", name);
        String metricNameExecutionFailure = String.format("%s.execution.failure", name);
        List<Future<StressTest>> stressTestTasks = new ArrayList<>(stressTests.size());

        for (StressTest stressTest : stressTests) {
            stressTestNotification.notifyStarting(stressTest);

            stressTestTasks.add(executorService.submit(() -> {
                stressTest.perform(metricDataCollector);

                return stressTest;
            }));
        }

        for (Future<StressTest> stressTestTask : stressTestTasks) {
            double interruptedFailure = 0D;
            double executionFailure = 0D;

            try {
                stressTestNotification.notifyCompleted(stressTestTask.get());
            } catch (InterruptedException e) {
                interruptedFailure = 1D;
                exceptions.add(e);
            } catch (ExecutionException e) {
                executionFailure = 1D;
                exceptions.add(e);
            } finally {
                metricDataCollector.add(metricNameInterruptedFailure, interruptedFailure, Unit.ONE);
                metricDataCollector.add(metricNameExecutionFailure, executionFailure, Unit.ONE);
            }
        }
    }

    @Override
    public Iterable<MetricDataSelector> extractMetricDataSelectors() {
        String metricNameInterruptedFailure = String.format("%s.interrupted.failure", name);
        MetricKey metricKeyInterruptedFailure = new MetricKeyDefault(metricNameInterruptedFailure, Unit.ONE);
        String metricNameExecutionFailure = String.format("%s.execution.failure", name);
        MetricKey metricKeyExecutionFailure = new MetricKeyDefault(metricNameExecutionFailure, Unit.ONE);

        Iterable<MetricDataSelector> metricDataSelectorsFromStressTest = stressTests.stream()
                .flatMap(st -> StreamSupport.stream(st.extractMetricDataSelectors().spliterator(), false))
                ::iterator;

        Iterable<MetricDataSelector> metricDataSelectors = ImmutableList.<MetricDataSelector>builder()
                .add(new MetricDataSelector(metricKeyInterruptedFailure, "sum"))
                .add(new MetricDataSelector(metricKeyInterruptedFailure, "avg"))
                .add(new MetricDataSelector(metricKeyExecutionFailure, "sum"))
                .add(new MetricDataSelector(metricKeyExecutionFailure, "avg"))
                .build();

        return Iterables.concat(metricDataSelectorsFromStressTest, metricDataSelectors);
    }

    @Override
    public Iterable<Throwable> extractExceptions() {
        Iterable<Throwable> exceptionsFromStressTests = stressTests.stream()
                .flatMap(st -> StreamSupport.stream(st.extractExceptions().spliterator(), false))
                ::iterator;

        return Iterables.concat(exceptionsFromStressTests, exceptions);
    }
}
