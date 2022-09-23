package com.dipasquale.ai.rl.neat.common.openai;

import com.dipasquale.ai.rl.neat.Neat;
import com.dipasquale.ai.rl.neat.NeatTrainer;
import com.dipasquale.ai.rl.neat.common.NeatTestSetup;
import com.dipasquale.ai.rl.neat.common.NeatTrainerFactory;
import com.dipasquale.synchronization.event.loop.ParallelEventLoop;
import lombok.AccessLevel;
import lombok.Builder;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public final class NeatTestSetupOpenAIGym extends NeatTestSetup {
    private final OpenAIGymTaskSetup task;
    private final boolean shouldVisualizeSolution;

    private NeatTestSetupOpenAIGym(final OpenAIGymTaskSetup task, final Set<Integer> genomeIds, final ParallelEventLoop eventLoop, final NeatTrainerFactory neatTrainerFactory, final boolean shouldTestPersistence, final boolean shouldVisualizeSolution) {
        super(task, genomeIds, eventLoop, neatTrainerFactory, shouldTestPersistence);
        this.task = task;
        this.shouldVisualizeSolution = shouldVisualizeSolution;
    }

    @Builder(access = AccessLevel.PUBLIC, builderMethodName = "openAIGymBuilder")
    private static NeatTestSetupOpenAIGym create(final OpenAIGymTaskSetup task, final ParallelEventLoop eventLoop, final NeatTrainerFactory trainerFactory, final boolean shouldTestPersistence, final boolean shouldVisualizeSolution) {
        Set<Integer> genomeIds = eventLoop != null
                ? Collections.newSetFromMap(new ConcurrentHashMap<>())
                : new HashSet<>();

        NeatTrainerFactory fixedTrainerFactory = trainerFactory == null
                ? Neat::createTrainer
                : trainerFactory;

        return new NeatTestSetupOpenAIGym(task, genomeIds, eventLoop, fixedTrainerFactory, shouldTestPersistence, shouldVisualizeSolution);
    }

    @Override
    public void assertTaskSolution(final NeatTrainer trainer) {
        super.assertTaskSolution(trainer);

        if (shouldVisualizeSolution) {
            task.visualize(trainer);
        }
    }
}
