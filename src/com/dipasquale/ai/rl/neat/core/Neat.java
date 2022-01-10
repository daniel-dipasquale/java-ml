package com.dipasquale.ai.rl.neat.core;

import com.dipasquale.ai.rl.neat.context.Context;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class Neat {
    static EvaluatorSettings createOverridableSettings() {
        return EvaluatorSettings.builder()
                .general(GeneralSupport.builder()
                        .genesisGenomeTemplate(GenesisGenomeTemplate.createDefault(1, 1)) // NOTE: shamelessly avoiding a null pointer exception instead of coming up with a better design
                        .fitnessFunction((IsolatedNeatEnvironment) genomeActivator -> 0f)
                        .build())
                .build();
    }

    public static NeatEvaluator createEvaluator(final EvaluatorSettings settings) {
        return new ConcurrentNeatEvaluator(settings.createContext());
    }

    public static NeatTrainer createTrainer(final EvaluatorSettings settings, final NeatTrainingPolicy trainingPolicy) {
        return new ConcurrentNeatTrainer(settings.createContext(), trainingPolicy);
    }

    public static NeatTrainer createTrainer(final InputStream inputStream, final EvaluatorOverrideSettings settings)
            throws IOException {
        NeatTrainer trainer = Neat.createTrainer(createOverridableSettings(), InvalidTrainingPolicy.getInstance());

        EvaluatorLoadSettings settingsFixed = EvaluatorLoadSettings.builder()
                .fitnessFunction(settings.getFitnessFunction())
                .eventLoop(settings.getEventLoop())
                .build();

        trainer.load(inputStream, settingsFixed);

        return trainer;
    }

    public static MultiNeatTrainer createMultiTrainer(final EvaluatorSettings settings, final NeatTrainingPolicy trainingPolicy) {
        Context.ParallelismSupport parallelismSupport = settings.getParallelism().create();

        EvaluatorSettings settingsFixed = EvaluatorSettings.builder()
                .general(settings.getGeneral())
                .parallelism(ParallelismSupport.builder()
                        .build())
                .random(settings.getRandom())
                .nodes(settings.getNodes())
                .connections(settings.getConnections())
                .activation(settings.getActivation())
                .mutation(settings.getMutation())
                .crossOver(settings.getCrossOver())
                .speciation(settings.getSpeciation())
                .metrics(settings.getMetrics())
                .build();

        List<Context> contexts = IntStream.range(0, settings.getParallelism().getConcurrencyLevel())
                .mapToObj(__ -> settingsFixed.createContext())
                .collect(Collectors.toList());

        return new ConcurrentNeatMultiTrainer(parallelismSupport, contexts, trainingPolicy);
    }
}
