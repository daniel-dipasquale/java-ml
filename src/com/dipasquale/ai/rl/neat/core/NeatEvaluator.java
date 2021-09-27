package com.dipasquale.ai.rl.neat.core;

import com.dipasquale.ai.rl.neat.settings.EvaluatorLoadSettings;
import com.dipasquale.ai.rl.neat.speciation.metric.IterationMetricData;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

public interface NeatEvaluator {
    int getIteration();

    int getGeneration();

    int getSpeciesCount();

    int getCurrentHiddenNodes();

    int getCurrentConnections();

    float getMaximumFitness();

    Map<Integer, IterationMetricData> getMetrics();

    void evaluateFitness();

    void evolve();

    void restart();

    float[] activate(float[] input);

    void save(OutputStream outputStream) throws IOException;

    void load(InputStream inputStream, EvaluatorLoadSettings settings) throws IOException;
}
