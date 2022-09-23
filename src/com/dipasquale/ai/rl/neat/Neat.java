package com.dipasquale.ai.rl.neat;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class Neat {
    public static NeatEvaluator createEvaluator(final NeatSettings settings) {
        return new ConcurrentNeatEvaluator(settings.createContext());
    }

    public static NeatTrainer createTrainer(final NeatSettings settings, final NeatTrainingPolicy trainingPolicy) {
        return new ConcurrentNeatTrainer(settings.createContext(), trainingPolicy);
    }

    public static NeatTrainer createTrainer(final InputStream inputStream, final NeatSettingsOverride settingsOverride)
            throws IOException {
        NeatLoadSettings loadSettings = NeatLoadSettings.builder()
                .fitnessFunction(settingsOverride.getFitnessFunction())
                .eventLoop(settingsOverride.getEventLoop())
                .build();

        return ConcurrentNeatTrainer.create(inputStream, loadSettings);
    }

    public static ParallelNeatTrainer createParallelTrainer(final NeatSettings settings, final NeatTrainingPolicy trainingPolicy) {
        Context.ParallelismSupport parallelismSupport = settings.getParallelism().create();

        NeatSettings fixedSettings = NeatSettings.builder()
                .general(settings.getGeneral())
                .parallelism(ParallelismSettings.builder()
                        .build())
                .randomness(settings.getRandomness())
                .nodeGenes(settings.getNodeGenes())
                .connectionGenes(settings.getConnectionGenes())
                .activation(settings.getActivation())
                .mutation(settings.getMutation())
                .crossOver(settings.getCrossOver())
                .speciation(settings.getSpeciation())
                .metrics(settings.getMetrics())
                .build();

        List<Context> contexts = IntStream.range(0, settings.getParallelism().getThreadIds().size())
                .mapToObj(__ -> fixedSettings.createContext())
                .collect(Collectors.toList());

        return new ConcurrentParallelNeatTrainer(parallelismSupport, contexts, trainingPolicy);
    }
}
