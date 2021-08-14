package com.dipasquale.data.structure.probabilistic;

import com.google.common.collect.ImmutableMap;
import com.google.common.hash.Hashing;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.Map;

@RequiredArgsConstructor
public final class DefaultHashingFunctionFactory implements HashingFunctionFactory, Serializable {
    @Serial
    private static final long serialVersionUID = -1040358716918215789L;
    private static final int SEED = 1_218_648_738;
    private static final long OFFSET = 3_847_382_443_595_522_248L;
    private final int seed;
    private final long offset;

    public DefaultHashingFunctionFactory() {
        this(SEED, OFFSET);
    }

    private static final Map<HashingFunctionAlgorithm, HashingFunctionFactoryProxy> HASHING_FUNCTION_FACTORIES = ImmutableMap.<HashingFunctionAlgorithm, HashingFunctionFactoryProxy>builder()
            .put(HashingFunctionAlgorithm.ADLER_32, (seed, offset, salt) -> new GuavaHashingFunction(Hashing.adler32(), offset, salt))
            .put(HashingFunctionAlgorithm.CRC_32, (seed, offset, salt) -> new GuavaHashingFunction(Hashing.crc32(), offset, salt))
            .put(HashingFunctionAlgorithm.CRC_32C, (seed, offset, salt) -> new GuavaHashingFunction(Hashing.crc32c(), offset, salt))
            .put(HashingFunctionAlgorithm.MD5, (seed, offset, salt) -> new DefaultHashingFunction(HashingFunctionAlgorithm.MD5, offset, salt))
            .put(HashingFunctionAlgorithm.MURMUR_3_32, (seed, offset, salt) -> new GuavaHashingFunction(Hashing.murmur3_32(seed), offset, salt))
            .put(HashingFunctionAlgorithm.MURMUR_3_128, (seed, offset, salt) -> new GuavaHashingFunction(Hashing.murmur3_128(seed), offset, salt))
            .put(HashingFunctionAlgorithm.SHA_1, (seed, offset, salt) -> new DefaultHashingFunction(HashingFunctionAlgorithm.SHA_1, offset, salt))
            .put(HashingFunctionAlgorithm.SHA_256, (seed, offset, salt) -> new DefaultHashingFunction(HashingFunctionAlgorithm.SHA_256, offset, salt))
            .put(HashingFunctionAlgorithm.SHA_512, (seed, offset, salt) -> new DefaultHashingFunction(HashingFunctionAlgorithm.SHA_512, offset, salt))
            .put(HashingFunctionAlgorithm.SIP_HASH_24, (seed, offset, salt) -> new GuavaHashingFunction(Hashing.sipHash24(), offset, salt))
            .build();

    @Override
    public HashingFunction create(final HashingFunctionAlgorithm algorithm, final byte[] salt) {
        return HASHING_FUNCTION_FACTORIES.get(algorithm).create(seed, offset, salt);
    }

    @FunctionalInterface
    private interface HashingFunctionFactoryProxy {
        HashingFunction create(int seed, long offset, byte[] salt);
    }
}
