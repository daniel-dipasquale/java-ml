package com.dipasquale.data.structure.probabilistic;

import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.EnumMap;
import java.util.Map;

@RequiredArgsConstructor
public final class DefaultHashingFunctionFactory implements HashingFunctionFactory, Serializable {
    @Serial
    private static final long serialVersionUID = -1040358716918215789L;
    private static final Map<HashingFunctionAlgorithm, HashingFunctionFactoryProxy> HASHING_FUNCTION_FACTORIES = createHashingFunctionFactories();
    private static final int SEED = 1_218_648_738;
    private static final long OFFSET = 3_847_382_443_595_522_248L;
    private final int seed;
    private final long offset;

    public DefaultHashingFunctionFactory() {
        this(SEED, OFFSET);
    }

    private static Map<HashingFunctionAlgorithm, HashingFunctionFactoryProxy> createHashingFunctionFactories() {
        Map<HashingFunctionAlgorithm, HashingFunctionFactoryProxy> hashingFunctionFactories = new EnumMap<>(HashingFunctionAlgorithm.class);

        hashingFunctionFactories.put(HashingFunctionAlgorithm.MD5, (seed, offset, salt) -> new ThreadLocalHashingFunction(HashingFunctionAlgorithm.MD5, offset, salt));
        hashingFunctionFactories.put(HashingFunctionAlgorithm.SHA_1, (seed, offset, salt) -> new ThreadLocalHashingFunction(HashingFunctionAlgorithm.SHA_1, offset, salt));
        hashingFunctionFactories.put(HashingFunctionAlgorithm.SHA_256, (seed, offset, salt) -> new ThreadLocalHashingFunction(HashingFunctionAlgorithm.SHA_256, offset, salt));
        hashingFunctionFactories.put(HashingFunctionAlgorithm.SHA_512, (seed, offset, salt) -> new ThreadLocalHashingFunction(HashingFunctionAlgorithm.SHA_512, offset, salt));

        return hashingFunctionFactories;
    }

    @Override
    public HashingFunction create(final HashingFunctionAlgorithm algorithm, final byte[] salt) {
        return HASHING_FUNCTION_FACTORIES.get(algorithm).create(seed, offset, salt);
    }

    @FunctionalInterface
    private interface HashingFunctionFactoryProxy {
        HashingFunction create(int seed, long offset, byte[] salt);
    }
}
