package com.dipasquale.ai.rl.neat.core;

import com.dipasquale.synchronization.event.loop.IterableEventLoop;
import lombok.AccessLevel;
import lombok.Builder;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

final class NeatTestSetupOpenAIGym extends NeatTestSetup {
    private final OpenAIGymTaskSetup task;
    private final boolean shouldVisualizeSolution;

    private NeatTestSetupOpenAIGym(final OpenAIGymTaskSetup task, final Set<String> genomeIds, final IterableEventLoop eventLoop, final NeatTrainerFactory neatTrainerFactory, final boolean shouldTestPersistence, final boolean shouldVisualizeSolution) {
        super(task, genomeIds, eventLoop, neatTrainerFactory, shouldTestPersistence);
        this.task = task;
        this.shouldVisualizeSolution = shouldVisualizeSolution;
    }

    @Builder(access = AccessLevel.PACKAGE, builderMethodName = "openAIGymBuilder")
    private static NeatTestSetupOpenAIGym create(final OpenAIGymTaskSetup task, final IterableEventLoop eventLoop, final NeatTrainerFactory neatTrainerFactory, final boolean shouldTestPersistence, final boolean shouldVisualizeSolution) {
        Set<String> genomeIds = eventLoop == null
                ? new HashSet<>()
                : Collections.newSetFromMap(new ConcurrentHashMap<>());

        NeatTrainerFactory neatTrainerFactoryFixed = neatTrainerFactory == null
                ? Neat::createTrainer
                : neatTrainerFactory;

        return new NeatTestSetupOpenAIGym(task, genomeIds, eventLoop, neatTrainerFactoryFixed, shouldTestPersistence, shouldVisualizeSolution);
    }

    @Override
    public void assertTaskSolution(final NeatTrainer trainer) {
        super.assertTaskSolution(trainer);

        if (shouldVisualizeSolution) {
            task.visualize(trainer);
        }
    }
}
