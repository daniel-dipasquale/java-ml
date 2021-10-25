package com.dipasquale.ai.rl.neat.core;

import com.dipasquale.synchronization.event.loop.IterableEventLoop;

import java.util.Set;

interface TaskSetup {
    String getName();

    boolean isMetricsEmissionEnabled();

    int getPopulationSize();

    EvaluatorSettings createSettings(Set<String> genomeIds, IterableEventLoop eventLoop);

    NeatTrainingPolicy createTrainingPolicy();
}
