package com.dipasquale.ai.common;

@FunctionalInterface
public interface NeuralNetworkEncoder<T> {
    float[] encode(T input);
}
