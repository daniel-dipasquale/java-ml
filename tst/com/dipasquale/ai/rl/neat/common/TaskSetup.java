package com.dipasquale.ai.rl.neat.common;

import com.dipasquale.ai.rl.neat.EvaluatorSettings;
import com.dipasquale.ai.rl.neat.NeatTrainingPolicy;
import com.dipasquale.synchronization.event.loop.BatchingEventLoop;

import java.util.Set;

public interface TaskSetup {
    String getName();

    int getPopulationSize();

    boolean isMetricsEmissionEnabled();

    EvaluatorSettings createSettings(Set<String> genomeIds, BatchingEventLoop eventLoop);

    NeatTrainingPolicy createTrainingPolicy();
}
