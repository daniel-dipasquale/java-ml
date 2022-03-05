package com.dipasquale.ai.rl.neat.common;

import com.dipasquale.ai.rl.neat.EvaluatorOverrideSettings;
import com.dipasquale.ai.rl.neat.EvaluatorSettings;
import com.dipasquale.ai.rl.neat.Neat;
import com.dipasquale.ai.rl.neat.NeatTrainer;
import com.dipasquale.ai.rl.neat.NeatTrainingPolicy;
import com.dipasquale.ai.rl.neat.NeatTrainingResult;
import com.dipasquale.ai.rl.neat.genotype.NodeGeneType;
import com.dipasquale.synchronization.event.loop.BatchingEventLoop;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class NeatTestSetup {
    @Getter
    private final TaskSetup task;
    private final Set<String> genomeIds;
    private final BatchingEventLoop eventLoop;
    private final NeatTrainerFactory neatTrainerFactory;
    private final boolean shouldTestPersistence;

    @Builder(access = AccessLevel.PUBLIC)
    private static NeatTestSetup create(final TaskSetup task, final BatchingEventLoop eventLoop, final NeatTrainerFactory neatTrainerFactory, final boolean shouldTestPersistence) {
        Set<String> genomeIds = eventLoop == null
                ? new HashSet<>()
                : Collections.newSetFromMap(new ConcurrentHashMap<>());

        NeatTrainerFactory neatTrainerFactoryFixed = neatTrainerFactory == null
                ? Neat::createTrainer
                : neatTrainerFactory;

        return new NeatTestSetup(task, genomeIds, eventLoop, neatTrainerFactoryFixed, shouldTestPersistence);
    }

    private static byte[] getBytes(final NeatTrainer trainer)
            throws IOException {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            trainer.save(outputStream);

            return outputStream.toByteArray();
        }
    }

    private static NeatTrainer createTrainer(final byte[] bytes, final EvaluatorOverrideSettings overrideSettings)
            throws IOException {
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes)) {
            return Neat.createTrainer(inputStream, overrideSettings);
        }
    }

    private NeatTrainerSetup createTrainerSetup() {
        return NeatTrainerSetup.builder()
                .name(task.getName())
                .genomeIds(genomeIds)
                .neatTrainerFactory(neatTrainerFactory)
                .eventLoop(eventLoop)
                .settings(task.createSettings(genomeIds, eventLoop))
                .trainingPolicy(task.createTrainingPolicy())
                .build();
    }

    private static void assertTrainingResults(final NeatTrainer trainer, final NeatTrainerSetup trainerSetup, final int populationSize) {
        System.out.printf("=========================================%n");
        System.out.printf("%s:%n", trainerSetup.name);
        System.out.printf("=========================================%n");

        boolean success = trainer.train();

        System.out.printf("iteration: %d%n", trainer.getState().getIteration());
        System.out.printf("generation: %d%n", trainer.getState().getGeneration());
        System.out.printf("species: %d%n", trainer.getState().getSpeciesCount());
        System.out.printf("hidden nodes: %d%n", trainer.getState().getChampionGenome().getNodes().size(NodeGeneType.HIDDEN));
        System.out.printf("expressed connections: %d%n", trainer.getState().getChampionGenome().getConnections().getExpressed().size());
        System.out.printf("total connections: %d%n", trainer.getState().getChampionGenome().getConnections().getAll().size());
        System.out.printf("maximum fitness: %f%n", trainer.getState().getMaximumFitness());
        Assertions.assertTrue(success);
        Assertions.assertEquals(populationSize, trainerSetup.genomeIds.size());
    }

    private static void assertPersistence(final NeatTrainer trainer, final NeatTrainerSetup trainerSetup) {
        try {
            byte[] bytes = getBytes(trainer);

            Assertions.assertTrue(bytes.length > 30_000); // TODO: work on adding an upper bound check, though remember that it will be higher if metrics are enabled

            EvaluatorOverrideSettings overrideSettings = EvaluatorOverrideSettings.builder()
                    .fitnessFunction(null)
                    .eventLoop(null)
                    .build();

            NeatTrainer trainerCopy = createTrainer(bytes, overrideSettings);

            Assertions.assertEquals(trainer.getState().getIteration(), trainerCopy.getState().getIteration());
            Assertions.assertEquals(trainer.getState().getGeneration(), trainerCopy.getState().getGeneration());
            Assertions.assertEquals(trainer.getState().getSpeciesCount(), trainerCopy.getState().getSpeciesCount());
            Assertions.assertEquals(trainer.getState().getChampionGenome(), trainerCopy.getState().getChampionGenome());
            Assertions.assertEquals(trainer.getState().getMaximumFitness(), trainerCopy.getState().getMaximumFitness(), 0f);
            Assertions.assertEquals(NeatTrainingResult.WORKING_SOLUTION_FOUND, trainerCopy.test());
            Assertions.assertEquals(NeatTrainingResult.WORKING_SOLUTION_FOUND, trainerCopy.test());
        } catch (IOException e) {
            Assertions.fail(e.getMessage());
        }
    }

    protected void assertTaskSolution(final NeatTrainer trainer) {
    }

    public final void assertTaskSolution() {
        NeatTrainerSetup trainerSetup = createTrainerSetup();
        NeatTrainer trainer = neatTrainerFactory.create(trainerSetup.settings, trainerSetup.trainingPolicy);
        int populationSize = task.getPopulationSize();

        assertTrainingResults(trainer, trainerSetup, populationSize);

        if (task.isMetricsEmissionEnabled()) {
            NeatMetricsReporter.displayMetrics(trainer);
        }

        if (shouldTestPersistence) {
            assertPersistence(trainer, trainerSetup);
        }

        assertTaskSolution(trainer);
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    @Builder(access = AccessLevel.PRIVATE)
    private static final class NeatTrainerSetup {
        private final String name;
        private final Set<String> genomeIds;
        private final NeatTrainerFactory neatTrainerFactory;
        private final BatchingEventLoop eventLoop;
        private final EvaluatorSettings settings;
        private final NeatTrainingPolicy trainingPolicy;
    }
}
