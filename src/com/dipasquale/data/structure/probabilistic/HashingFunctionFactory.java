/*
 * java-ml
 * (c) 2021 daniel-dipasquale
 * released under the MIT license
 */

package com.dipasquale.data.structure.probabilistic;

import java.nio.charset.StandardCharsets;

@FunctionalInterface
public interface HashingFunctionFactory {
    HashingFunction create(HashingFunctionAlgorithm algorithm, byte[] salt);

    default HashingFunction create(final HashingFunctionAlgorithm algorithm, final String salt) {
        return create(algorithm, salt.getBytes(StandardCharsets.UTF_8));
    }
}
