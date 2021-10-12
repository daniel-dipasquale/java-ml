package com.dipasquale.data.structure.probabilistic;

@FunctionalInterface
public interface HashingFunction {
    long hashCode(int hashCode, int entropy);
}
