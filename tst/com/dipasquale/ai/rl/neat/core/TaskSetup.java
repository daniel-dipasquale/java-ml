package com.dipasquale.ai.rl.neat.core;

import com.dipasquale.synchronization.event.loop.IterableEventLoop;

import java.util.Set;

interface TaskSetup {
    String getName();

    int getPopulationSize();

    boolean isMetricsEmissionEnabled();

    EvaluatorSettings createSettings(Set<String> genomeIds, IterableEventLoop eventLoop);

    NeatTrainingPolicy createTrainingPolicy();
}
