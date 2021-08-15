package com.dipasquale.ai.rl.neat.core;

import com.dipasquale.ai.rl.neat.settings.EvaluatorSettings;
import com.dipasquale.threading.event.loop.IterableEventLoop;

import java.util.Set;

interface TaskSetup {
    String getName();

    int getPopulationSize();

    EvaluatorSettings createSettings(Set<String> genomeIds, IterableEventLoop eventLoop);

    NeatTrainingPolicy createTrainingPolicy();
}
