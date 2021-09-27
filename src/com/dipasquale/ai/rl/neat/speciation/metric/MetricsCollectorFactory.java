package com.dipasquale.ai.rl.neat.speciation.metric;

import com.dipasquale.common.factory.data.structure.map.MapFactory;

@FunctionalInterface
public interface MetricsCollectorFactory {
    MetricsCollector create(MapFactory mapFactory, GenerationMetrics generationMetrics, FitnessMetrics fitnessMetrics, IterationMetrics iterationMetrics);
}
