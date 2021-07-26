package com.dipasquale.ai.common.fitness;

@FunctionalInterface
public interface FitnessFunction<T> {
    float test(T value);
}
