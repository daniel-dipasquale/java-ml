package com.dipasquale.ai.rl.neat;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface NeatEvaluatorTrainer {
    int getGeneration();

    int getSpeciesCount();

    float getMaximumFitness();

    boolean train(NeatEvaluatorTrainingPolicy trainingPolicy);

    float[] activate(float[] input);

    void save(OutputStream outputStream) throws IOException;

    void load(InputStream inputStream, SettingsEvaluatorState settings) throws IOException;
}
