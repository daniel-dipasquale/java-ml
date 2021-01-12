package com.experimental.stress;

import com.dipasquale.common.DateTimeSupport;
import com.experimental.metrics.MetricDataSelector;
import com.experimental.metrics.MetricKey;
import com.experimental.metrics.MetricKeyDefault;
import com.google.common.collect.ImmutableList;
import lombok.Builder;

import javax.measure.unit.Unit;
import java.util.function.Supplier;

public class StressTestSupplier extends StressTestStrategy {
    private StressTestSupplier(final StressTest stressTest) {
        super(stressTest);
    }

    @Builder
    public static <TInput, TOutput extends Number> StressTestSupplier create(final String serviceName,
                                                                             final String operationName,
                                                                             final Supplier<TOutput> supplier,
                                                                             final Iterable<TInput> iterable,
                                                                             final DateTimeSupport dateTimeSupport) {
        String metricNameNumber = String.format("%s.%s.number", serviceName, operationName);

        StressTestLambda<TInput, TOutput> stressTest = StressTestLambda.<TInput, TOutput>builder()
                .serviceName(serviceName)
                .operationName(operationName)
                .function(i -> supplier.get())
                .iterable(iterable)
                .dateTimeSupport(dateTimeSupport)
                .outputMetricAdder((o, mdc) -> {
                    if (o != null) {
                        mdc.add(metricNameNumber, o.doubleValue(), Unit.ONE);
                    }
                })
                .outputMetricGetter(() -> {
                    MetricKey metricKeyNumber = new MetricKeyDefault(metricNameNumber, Unit.ONE);

                    return ImmutableList.<MetricDataSelector>builder()
                            .add(new MetricDataSelector(metricKeyNumber, "sum"))
                            .add(new MetricDataSelector(metricKeyNumber, "avg"))
                            .build();
                })
                .build();

        return new StressTestSupplier(stressTest);
    }
}
