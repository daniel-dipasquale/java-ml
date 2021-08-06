package com.dipasquale.data.structure.probabilistic;

public interface MultiHashingFunction {
    int getMaximumAllowed();

    long hashCode(int hashCode, int hashFunctionIndex);
}
