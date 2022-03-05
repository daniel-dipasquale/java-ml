package com.dipasquale.ai.common;

@FunctionalInterface
public interface NeuralNetworkDecoder<TResult, TContext> {
    TResult decode(TContext context, float[] output);
}
