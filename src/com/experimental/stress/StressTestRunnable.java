package com.experimental.stress;

import com.dipasquale.common.DateTimeSupport;
import lombok.Builder;

public final class StressTestRunnable extends StressTestStrategy {
    private StressTestRunnable(final StressTest stressTest) {
        super(stressTest);
    }

    @Builder
    public static <T> StressTest create(final String serviceName,
                                        final String operationName,
                                        final Runnable runnable,
                                        final Iterable<T> iterable,
                                        final DateTimeSupport dateTimeSupport) {
        StressTestLambda<T, Object> stressTest = StressTestLambda.<T, Object>builder()
                .serviceName(serviceName)
                .operationName(operationName)
                .function(i -> {
                    runnable.run();

                    return null;
                })
                .iterable(iterable)
                .dateTimeSupport(dateTimeSupport)
                .build();

        return new StressTestRunnable(stressTest);
    }
}
