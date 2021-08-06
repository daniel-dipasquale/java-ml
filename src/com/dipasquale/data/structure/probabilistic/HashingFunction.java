package com.dipasquale.data.structure.probabilistic;

@FunctionalInterface
interface HashingFunction {
    long hashCode(int hashCode, int hashFunctionIndex);
}
