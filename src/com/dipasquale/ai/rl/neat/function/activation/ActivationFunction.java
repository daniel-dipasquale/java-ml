package com.dipasquale.ai.rl.neat.function.activation;

public interface ActivationFunction {
    float forward(float input);

    String toString();
}