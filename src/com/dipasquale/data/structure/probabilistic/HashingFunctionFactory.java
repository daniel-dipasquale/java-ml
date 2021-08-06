package com.dipasquale.data.structure.probabilistic;

@FunctionalInterface
interface HashingFunctionFactory {
    HashingFunction create(byte[] salt);
}
