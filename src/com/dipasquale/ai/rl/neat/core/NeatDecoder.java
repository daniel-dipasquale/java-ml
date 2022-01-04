package com.dipasquale.ai.rl.neat.core;

@FunctionalInterface
public interface NeatDecoder<TResult, TContext> {
    TResult decode(TContext context, float[] output);
}
