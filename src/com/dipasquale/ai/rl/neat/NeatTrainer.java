package com.dipasquale.ai.rl.neat;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface NeatTrainer extends NeatActivator {
    boolean train();

    NeatTrainingResult test();

    void save(OutputStream outputStream) throws IOException;

    void load(InputStream inputStream, EvaluatorLoadSettings settings) throws IOException;
}
