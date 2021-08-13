/*
 * java-ml
 * (c) 2021 daniel-dipasquale
 * released under the MIT license
 */

package com.dipasquale.data.structure.probabilistic;

@FunctionalInterface
public interface HashingFunction {
    long hashCode(int itemHashCode, int entropyId);
}
