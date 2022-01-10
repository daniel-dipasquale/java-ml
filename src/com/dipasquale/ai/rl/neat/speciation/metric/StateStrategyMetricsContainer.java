package com.dipasquale.ai.rl.neat.speciation.metric;

import com.dipasquale.common.factory.data.structure.map.MapFactory;
import com.dipasquale.metric.MetricDatumFactory;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;

@AllArgsConstructor
public final class StateStrategyMetricsContainer implements MetricsContainer, Serializable {
    @Serial
    private static final long serialVersionUID = -7518500811630022903L;
    private final MapFactory mapFactory;
    private final MetricDatumFactory metricDatumFactory;
    @Getter
    private FitnessMetrics fitnessMetrics;
    @Getter
    private GenerationMetrics generationMetrics;
    @Getter
    private IterationMetrics iterationMetrics;

    public StateStrategyMetricsContainer(final MapFactory mapFactory, final MetricDatumFactory metricDatumFactory) {
        this(mapFactory, metricDatumFactory, MetricsContainer.createFitnessMetrics(mapFactory, metricDatumFactory), MetricsContainer.createGenerationMetrics(mapFactory, metricDatumFactory), MetricsContainer.createIterationMetrics(mapFactory));
    }

    private FitnessMetrics createFitnessMetrics() {
        return MetricsContainer.createFitnessMetrics(mapFactory, metricDatumFactory);
    }

    private GenerationMetrics createGenerationMetrics() {
        return MetricsContainer.createGenerationMetrics(mapFactory, metricDatumFactory);
    }

    private IterationMetrics createIterationMetrics() {
        return MetricsContainer.createIterationMetrics(mapFactory);
    }

    public FitnessMetrics replaceFitnessMetrics() {
        return fitnessMetrics = createFitnessMetrics();
    }

    public GenerationMetrics replaceGenerationMetrics() {
        return generationMetrics = createGenerationMetrics();
    }

    public IterationMetrics replaceIterationMetrics() {
        return iterationMetrics = createIterationMetrics();
    }
}
