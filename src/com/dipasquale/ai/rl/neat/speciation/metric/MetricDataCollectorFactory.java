package com.dipasquale.ai.rl.neat.speciation.metric;

import com.dipasquale.common.factory.data.structure.map.MapFactory;

@FunctionalInterface
public interface MetricDataCollectorFactory {
    MetricDataCollector create(MapFactory mapFactory, GenerationMetricData generationMetrics, FitnessMetricData fitnessMetrics, IterationMetricData iterationMetrics);
}
