package com.experimental.stress;

import com.dipasquale.common.time.DateTimeSupport;
import com.experimental.metrics.MetricDataSelector;
import com.experimental.metrics.MetricKey;
import com.experimental.metrics.MetricKeyDefault;
import com.google.common.collect.ImmutableList;
import lombok.Builder;

import javax.measure.unit.Unit;
import java.util.function.Predicate;

public final class StressTestPredicate extends StressTestStrategy {
    private StressTestPredicate(final StressTest stressTest) {
        super(stressTest);
    }

    @Builder
    public static <T> StressTest create(final String serviceName,
                                        final String operationName,
                                        final Predicate<T> predicate,
                                        final Iterable<T> iterable,
                                        final DateTimeSupport dateTimeSupport) {
        String metricNamePositive = String.format("%s.%s.positive", serviceName, operationName);
        String metricNameNegative = String.format("%s.%s.negative", serviceName, operationName);

        StressTestLambda<T, Boolean> stressTest = StressTestLambda.<T, Boolean>builder()
                .serviceName(serviceName)
                .operationName(operationName)
                .function(predicate::test)
                .iterable(iterable)
                .dateTimeSupport(dateTimeSupport)
                .outputMetricAdder((o, mdc) -> {
                    mdc.add(metricNamePositive, o != null && o ? 1D : 0D, Unit.ONE);
                    mdc.add(metricNameNegative, o != null && !o ? 1D : 0D, Unit.ONE);
                })
                .outputMetricGetter(() -> {
                    MetricKey metricKeyPositive = new MetricKeyDefault(metricNamePositive, Unit.ONE);
                    MetricKey metricKeyNegative = new MetricKeyDefault(metricNameNegative, Unit.ONE);

                    return ImmutableList.<MetricDataSelector>builder()
                            .add(new MetricDataSelector(metricKeyPositive, "sum"))
                            .add(new MetricDataSelector(metricKeyPositive, "avg"))
                            .add(new MetricDataSelector(metricKeyNegative, "sum"))
                            .add(new MetricDataSelector(metricKeyNegative, "avg"))
                            .build();
                })
                .build();

        return new StressTestPredicate(stressTest);
    }
}
