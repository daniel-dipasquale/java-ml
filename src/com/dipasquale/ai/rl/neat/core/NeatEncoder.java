package com.dipasquale.ai.rl.neat.core;

@FunctionalInterface
public interface NeatEncoder<T> {
    float[] encode(T input);
}
