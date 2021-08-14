package com.dipasquale.data.structure.probabilistic;

@FunctionalInterface
public interface HashingFunction {
    long hashCode(int itemHashCode, int entropyId);
}
