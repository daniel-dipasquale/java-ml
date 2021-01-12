package com.experimental.stress;

import com.dipasquale.common.DateTimeSupport;
import com.experimental.metrics.MetricDataCollector;
import com.experimental.metrics.MetricDataSelector;
import com.experimental.metrics.MetricKey;
import com.experimental.metrics.MetricKeyDefault;
import com.google.common.collect.ImmutableList;
import lombok.Builder;

import javax.measure.quantity.Duration;
import javax.measure.unit.Unit;
import java.util.LinkedList;
import java.util.Optional;
import java.util.Queue;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

@Builder
public final class StressTestLambda<TInput, TOutput> implements StressTest {
    private final String serviceName;
    private final String operationName;
    private final Function<TInput, TOutput> function;
    private final Iterable<TInput> iterable;
    private final DateTimeSupport dateTimeSupport;
    private final Queue<Throwable> exceptions = new LinkedList<>();
    private final BiConsumer<TOutput, MetricDataCollector> outputMetricAdder;
    private final Supplier<Iterable<MetricDataSelector>> outputMetricGetter;

    @Override
    public String getName() {
        return serviceName;
    }

    public void perform(final MetricDataCollector metricDataCollector) {
        String metricNameTime = String.format("%s.%s.time", serviceName, operationName);
        String metricNameSuccess = String.format("%s.%s.success", serviceName, operationName);
        Unit<Duration> timeUnit = dateTimeSupport.unit();

        for (TInput item : iterable) {
            double success = 1D;
            long startDateTime = dateTimeSupport.now();
            TOutput output = null;

            try {
                output = function.apply(item);
            } catch (Throwable e) {
                success = 0D;
                exceptions.add(e);
            } finally {
                metricDataCollector.add(metricNameTime, dateTimeSupport.now() - startDateTime, timeUnit);
                metricDataCollector.add(metricNameSuccess, success, Unit.ONE);

                if (outputMetricAdder != null) {
                    outputMetricAdder.accept(output, metricDataCollector);
                }
            }
        }
    }

    @Override
    public Iterable<MetricDataSelector> extractMetricDataSelectors() {
        String metricNameTime = String.format("%s.%s.time", serviceName, operationName);
        MetricKey metricKeyTime = new MetricKeyDefault(metricNameTime, dateTimeSupport.unit());
        String metricNameSuccess = String.format("%s.%s.success", serviceName, operationName);
        MetricKey metricKeySuccess = new MetricKeyDefault(metricNameSuccess, Unit.ONE);

        return ImmutableList.<MetricDataSelector>builder()
                .add(new MetricDataSelector(metricKeyTime, "sum"))
                .add(new MetricDataSelector(metricKeyTime, "avg"))
                .add(new MetricDataSelector(metricKeyTime, "min"))
                .add(new MetricDataSelector(metricKeyTime, "p50"))
                .add(new MetricDataSelector(metricKeyTime, "p90"))
                .add(new MetricDataSelector(metricKeyTime, "p95"))
                .add(new MetricDataSelector(metricKeyTime, "p99"))
                .add(new MetricDataSelector(metricKeyTime, "p99.9"))
                .add(new MetricDataSelector(metricKeyTime, "p99.99"))
                .add(new MetricDataSelector(metricKeyTime, "max"))
                .add(new MetricDataSelector(metricKeySuccess, "sum"))
                .add(new MetricDataSelector(metricKeySuccess, "avg"))
                .addAll(Optional.ofNullable(outputMetricGetter)
                        .map(Supplier::get)
                        .orElseGet(ImmutableList::of))
                .build();
    }

    @Override
    public Iterable<Throwable> extractExceptions() {
        return exceptions.stream()::iterator;
    }
}
