package com.dipasquale.ai.rl.neat.core;

import com.dipasquale.ai.rl.neat.settings.EvaluatorLoadSettings;
import com.dipasquale.ai.rl.neat.settings.EvaluatorOverrideSettings;
import com.dipasquale.ai.rl.neat.settings.EvaluatorSettings;
import com.dipasquale.ai.rl.neat.settings.GeneralEvaluatorSupport;
import com.dipasquale.ai.rl.neat.settings.GenesisGenomeTemplate;

import java.io.IOException;
import java.io.InputStream;

public interface Neat {
    static NeatEvaluator createEvaluator(final EvaluatorSettings settings) {
        return new ConcurrentNeatEvaluator(settings.createContext());
    }

    static NeatTrainer createTrainer(final EvaluatorSettings settings) {
        return new ConcurrentNeatTrainer(settings.createContext());
    }

    static NeatTrainer createTrainer(final InputStream inputStream, final EvaluatorOverrideSettings overrideSettings)
            throws IOException {
        EvaluatorSettings settings = EvaluatorSettings.builder()
                .general(GeneralEvaluatorSupport.builder()
                        .genesisGenomeFactory(GenesisGenomeTemplate.createDefault(1, 1)) // NOTE: shamelessly avoiding a null pointer exception instead of coming up with a better design
                        .fitnessFunction(g -> 0f)
                        .build())
                .build();

        NeatTrainer trainer = Neat.createTrainer(settings);

        EvaluatorLoadSettings loadSettings = EvaluatorLoadSettings.builder()
                .fitnessFunction(overrideSettings.getFitnessFunction())
                .eventLoop(overrideSettings.getEventLoop())
                .build();

        trainer.load(inputStream, loadSettings);

        return trainer;
    }
}
