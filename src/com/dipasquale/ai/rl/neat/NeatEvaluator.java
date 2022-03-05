package com.dipasquale.ai.rl.neat;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface NeatEvaluator extends NeatActivator {
    void evaluateFitness();

    void evolve();

    void restart();

    void save(OutputStream outputStream) throws IOException;

    void load(InputStream inputStream, EvaluatorLoadSettings settings) throws IOException;
}
