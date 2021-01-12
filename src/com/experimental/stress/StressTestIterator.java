package com.experimental.stress;

import com.dipasquale.common.DateTimeSupport;
import com.experimental.metrics.MetricDataSelector;
import com.experimental.metrics.MetricKey;
import com.experimental.metrics.MetricKeyDefault;
import com.google.common.collect.ImmutableList;
import lombok.Builder;

import javax.measure.unit.Unit;
import java.util.function.Supplier;

public final class StressTestIterator extends StressTestStrategy {
    private StressTestIterator(final StressTest stressTest) {
        super(stressTest);
    }

    @Builder
    public static <T> StressTest create(final String serviceName,
                                        final String operationName,
                                        final Supplier<? extends Iterable<T>> supplier,
                                        final Iterable<T> iterable,
                                        final DateTimeSupport dateTimeSupport) {
        String metricNameIteratorCount = String.format("%s.%s.count", serviceName, operationName);

        StressTestLambda<T, Integer> stressTest = StressTestLambda.<T, Integer>builder()
                .serviceName(serviceName)
                .operationName(operationName)
                .function(i -> ImmutableList.copyOf(supplier.get()).size())
                .iterable(iterable)
                .dateTimeSupport(dateTimeSupport)
                .outputMetricAdder((o, mdc) -> mdc.add(metricNameIteratorCount, o != null ? (double) o : 0D, Unit.ONE))
                .outputMetricGetter(() -> {
                    MetricKey metricKeyIteratorCount = new MetricKeyDefault(metricNameIteratorCount, Unit.ONE);

                    return ImmutableList.<MetricDataSelector>builder()
                            .add(new MetricDataSelector(metricKeyIteratorCount, "sum"))
                            .add(new MetricDataSelector(metricKeyIteratorCount, "avg"))
                            .build();
                })
                .build();

        return new StressTestIterator(stressTest);
    }
}
