package com.dipasquale.ai.rl.neat.synchronization.dual.mode.speciation.metric;

import com.dipasquale.ai.rl.neat.speciation.metric.FitnessMetrics;
import com.dipasquale.ai.rl.neat.speciation.metric.GenerationMetrics;
import com.dipasquale.ai.rl.neat.speciation.metric.IterationMetrics;
import com.dipasquale.ai.rl.neat.speciation.metric.MetricsContainer;
import com.dipasquale.ai.rl.neat.speciation.metric.StateStrategyMetricsContainer;
import com.dipasquale.metric.MetricDatumFactory;
import com.dipasquale.synchronization.dual.mode.DualModeObject;
import com.dipasquale.synchronization.dual.mode.data.structure.map.DualModeMapFactory;

import java.io.Serial;
import java.io.Serializable;

public final class DualModeMetricsContainer implements MetricsContainer, DualModeObject, Serializable {
    @Serial
    private static final long serialVersionUID = -4469062666149439132L;
    private final DualModeMapFactory mapFactory;
    private final MetricDatumFactory metricDatumFactory;
    private StateStrategyMetricsContainer metricsContainer;

    public DualModeMetricsContainer(final DualModeMapFactory mapFactory, final MetricDatumFactory metricDatumFactory) {
        this.mapFactory = mapFactory;
        this.metricDatumFactory = metricDatumFactory;
        this.metricsContainer = new StateStrategyMetricsContainer(mapFactory, metricDatumFactory);
    }

    @Override
    public FitnessMetrics getFitnessMetrics() {
        return metricsContainer.getFitnessMetrics();
    }

    @Override
    public FitnessMetrics replaceFitnessMetrics() {
        return metricsContainer.replaceFitnessMetrics();
    }

    @Override
    public GenerationMetrics getGenerationMetrics() {
        return metricsContainer.getGenerationMetrics();
    }

    @Override
    public GenerationMetrics replaceGenerationMetrics() {
        return metricsContainer.replaceGenerationMetrics();
    }

    @Override
    public IterationMetrics getIterationMetrics() {
        return metricsContainer.getIterationMetrics();
    }

    @Override
    public IterationMetrics replaceIterationMetrics() {
        return metricsContainer.replaceIterationMetrics();
    }

    private StateStrategyMetricsContainer createMetricsContainer() {
        FitnessMetrics fitnessMetrics = metricsContainer.getFitnessMetrics().createCopy(mapFactory);
        GenerationMetrics generationMetrics = metricsContainer.getGenerationMetrics().createCopy(mapFactory);
        IterationMetrics iterationMetrics = metricsContainer.getIterationMetrics().createCopy(mapFactory);

        return new StateStrategyMetricsContainer(mapFactory, metricDatumFactory, fitnessMetrics, generationMetrics, iterationMetrics);
    }

    @Override
    public void activateMode(final int concurrencyLevel) {
        mapFactory.activateMode(concurrencyLevel);
        metricsContainer = createMetricsContainer();
    }
}
