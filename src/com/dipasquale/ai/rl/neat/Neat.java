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
    private static final GenesisGenomeTemplate AVOID_NULL_POINTER_GENESIS_GENOME_TEMPLATE = GenesisGenomeTemplate.builder()
            .inputs(1)
            .outputs(1)
            .build();

    static EvaluatorSettings createOverridableSettings() {
        return EvaluatorSettings.builder()
                .general(GeneralSupport.builder()
                        .genesisGenomeTemplate(AVOID_NULL_POINTER_GENESIS_GENOME_TEMPLATE)
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

        EvaluatorLoadSettings fixedSettings = EvaluatorLoadSettings.builder()
                .fitnessFunction(settings.getFitnessFunction())
                .eventLoop(settings.getEventLoop())
                .build();

        trainer.load(inputStream, fixedSettings);

        return trainer;
    }

    public static ParallelNeatTrainer createParallelTrainer(final EvaluatorSettings settings, final NeatTrainingPolicy trainingPolicy) {
        Context.ParallelismSupport parallelismSupport = settings.getParallelism().create();

        EvaluatorSettings fixedSettings = EvaluatorSettings.builder()
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
                .mapToObj(__ -> fixedSettings.createContext())
                .collect(Collectors.toList());

        return new ConcurrentParallelNeatTrainer(parallelismSupport, contexts, trainingPolicy);
    }
}
