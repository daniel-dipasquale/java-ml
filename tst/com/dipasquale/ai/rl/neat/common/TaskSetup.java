package com.dipasquale.ai.rl.neat.common;

import com.dipasquale.ai.rl.neat.NeatSettings;
import com.dipasquale.ai.rl.neat.NeatTrainingPolicy;
import com.dipasquale.synchronization.event.loop.ParallelEventLoop;

import java.util.Set;

public interface TaskSetup {
    String getName();

    int getPopulationSize();

    boolean isMetricsEmissionEnabled();

    NeatSettings createSettings(Set<Integer> genomeIds, ParallelEventLoop eventLoop);

    NeatTrainingPolicy createTrainingPolicy();
}
