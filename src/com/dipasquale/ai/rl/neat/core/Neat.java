package com.dipasquale.ai.rl.neat.core;

import com.dipasquale.ai.rl.neat.settings.EvaluatorSettings;
import com.dipasquale.ai.rl.neat.settings.EvaluatorStateSettings;
import com.dipasquale.ai.rl.neat.settings.GeneralEvaluatorSupportSettings;
import com.dipasquale.ai.rl.neat.settings.GenesisGenomeTemplateSettings;

import java.io.IOException;
import java.io.InputStream;

public interface Neat {
    static NeatEvaluator createEvaluator(final EvaluatorSettings settings) {
        return new NeatEvaluatorSynchronized(settings.createContext());
    }

    static NeatEvaluatorTrainer createEvaluatorTrainer(final EvaluatorSettings settings) {
        return new NeatEvaluatorTrainerSynchronized(settings.createContext());
    }

    static NeatEvaluatorTrainer createEvaluatorTrainer(final InputStream inputStream, final EvaluatorStateSettings settings)
            throws IOException {
        EvaluatorSettings evaluatorTrainerSettings = EvaluatorSettings.builder()
                .general(GeneralEvaluatorSupportSettings.builder()
                        .genesisGenomeFactory(GenesisGenomeTemplateSettings.createDefault(1, 1))
                        .fitnessFunction(g -> 0f)
                        .build())
                .build();

        NeatEvaluatorTrainer evaluatorTrainer = Neat.createEvaluatorTrainer(evaluatorTrainerSettings);

        evaluatorTrainer.load(inputStream, settings);

        return evaluatorTrainer;
    }
}
