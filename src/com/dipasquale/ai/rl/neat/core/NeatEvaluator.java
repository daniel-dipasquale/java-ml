package com.dipasquale.ai.rl.neat.core;

import com.dipasquale.ai.rl.neat.settings.EvaluatorLoadSettings;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface NeatEvaluator {
    int getGeneration();

    int getSpeciesCount();

    int getCurrentConnections();

    float getMaximumFitness();

    void evaluateFitness();

    void evolve();

    void restart();

    float[] activate(float[] input);

    void save(OutputStream outputStream) throws IOException;

    void load(InputStream inputStream, EvaluatorLoadSettings settings) throws IOException;
}
