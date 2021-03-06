package com.dipasquale.ai.rl.neat;

@FunctionalInterface
public interface Environment {
    float test(Genome genome);
}
