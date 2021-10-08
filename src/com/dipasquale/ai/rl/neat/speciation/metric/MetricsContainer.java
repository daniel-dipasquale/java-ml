package com.dipasquale.ai.rl.neat.speciation.metric;

import com.dipasquale.common.factory.data.structure.map.MapFactory;
import com.dipasquale.metric.MetricDatumFactory;

import java.util.ArrayList;

public interface MetricsContainer {
    FitnessMetrics getFitnessMetrics();

    FitnessMetrics replaceFitnessMetrics();

    GenerationMetrics getGenerationMetrics();

    GenerationMetrics replaceGenerationMetrics();

    IterationMetrics getIterationMetrics();

    IterationMetrics replaceIterationMetrics();

    static TopologyMetrics createTopologyMetrics(final MetricDatumFactory metricDatumFactory) {
        return new TopologyMetrics(metricDatumFactory.create(), metricDatumFactory.create());
    }

    static FitnessMetrics createFitnessMetrics(final MapFactory mapFactory, final MetricDatumFactory metricDatumFactory) {
        return new FitnessMetrics(mapFactory.create(), metricDatumFactory.create());
    }

    static GenerationMetrics createGenerationMetrics(final MapFactory mapFactory, final MetricDatumFactory metricDatumFactory) {
        return new GenerationMetrics(mapFactory.create(), new ArrayList<>(), metricDatumFactory.create(), metricDatumFactory.create(), metricDatumFactory.create(), mapFactory.create(), metricDatumFactory.create());
    }

    static IterationMetrics createIterationMetrics(final MapFactory mapFactory) {
        return new IterationMetrics(mapFactory.create());
    }
}
