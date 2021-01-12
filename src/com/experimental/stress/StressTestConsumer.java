package com.experimental.stress;

import com.dipasquale.common.DateTimeSupport;
import lombok.Builder;

import java.util.function.Consumer;

public final class StressTestConsumer extends StressTestStrategy {
    private StressTestConsumer(final StressTest stressTest) {
        super(stressTest);
    }

    @Builder
    public static <T> StressTest create(final String serviceName,
                                        final String operationName,
                                        final Consumer<T> consumer,
                                        final Iterable<T> iterable,
                                        final DateTimeSupport dateTimeSupport) {
        StressTestLambda<T, Object> stressTest = StressTestLambda.<T, Object>builder()
                .serviceName(serviceName)
                .operationName(operationName)
                .function(i -> {
                    consumer.accept(i);

                    return null;
                })
                .iterable(iterable)
                .dateTimeSupport(dateTimeSupport)
                .build();

        return new StressTestConsumer(stressTest);
    }
}
