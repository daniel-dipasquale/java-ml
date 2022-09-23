package com.dipasquale.ai.rl.neat.speciation.metric;

import com.dipasquale.metric.MetricDatumFactory;

public final class StandardMetricsContainer extends AbstractMetricsContainer {
    public StandardMetricsContainer(final MetricDatumFactory metricDatumFactory, final IterationMetrics iterationMetrics) {
        super(metricDatumFactory, iterationMetrics);
    }
}
