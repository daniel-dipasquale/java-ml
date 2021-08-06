package com.dipasquale.data.structure.probabilistic;

import com.google.common.collect.ImmutableMap;
import com.google.common.hash.Hashing;
import lombok.Getter;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serial;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public final class DefaultMultiHashingFunction implements MultiHashingFunction, Serializable {
    @Serial
    private static final long serialVersionUID = 409943100796909354L;
    private static final long OFFSET = 3_847_382_443_595_522_248L;
    private static final int SEED = 1_218_648_738;

    private static final Map<HashingFunctionAlgorithm, HashingFunctionFactory> HASHING_FUNCTION_FACTORIES = ImmutableMap.<HashingFunctionAlgorithm, HashingFunctionFactory>builder()
            .put(HashingFunctionAlgorithm.ADLER_32, (salt) -> new GuavaHashingFunction(Hashing.adler32(), OFFSET, salt))
            .put(HashingFunctionAlgorithm.CRC_32, (salt) -> new GuavaHashingFunction(Hashing.crc32(), OFFSET, salt))
            .put(HashingFunctionAlgorithm.CRC_32C, (salt) -> new GuavaHashingFunction(Hashing.crc32c(), OFFSET, salt))
            .put(HashingFunctionAlgorithm.MD5, (salt) -> new DefaultHashingFunction(HashingFunctionAlgorithm.MD5, OFFSET, salt))
            .put(HashingFunctionAlgorithm.MURMUR_3_32, (salt) -> new GuavaHashingFunction(Hashing.murmur3_32(SEED), OFFSET, salt))
            .put(HashingFunctionAlgorithm.MURMUR_3_128, (salt) -> new GuavaHashingFunction(Hashing.murmur3_128(SEED), OFFSET, salt))
            .put(HashingFunctionAlgorithm.SHA_1, (salt) -> new DefaultHashingFunction(HashingFunctionAlgorithm.SHA_1, OFFSET, salt))
            .put(HashingFunctionAlgorithm.SHA_256, (salt) -> new DefaultHashingFunction(HashingFunctionAlgorithm.SHA_256, OFFSET, salt))
            .put(HashingFunctionAlgorithm.SHA_512, (salt) -> new DefaultHashingFunction(HashingFunctionAlgorithm.SHA_512, OFFSET, salt))
            .put(HashingFunctionAlgorithm.SIP_HASH_24, (salt) -> new GuavaHashingFunction(Hashing.sipHash24(), OFFSET, salt))
            .build();

    @Getter
    private final int maximumAllowed;
    private final HashingFunctionAlgorithm algorithm;
    private final byte[] salt;
    private transient ThreadLocal<HashingFunction> hashCodeFunction;

    public DefaultMultiHashingFunction(final int maximumAllowed, final HashingFunctionAlgorithm algorithm, final byte[] salt) {
        this.maximumAllowed = maximumAllowed;
        this.algorithm = algorithm;
        this.salt = salt;
        this.hashCodeFunction = ThreadLocal.withInitial(() -> HASHING_FUNCTION_FACTORIES.get(algorithm).create(salt));
    }

    public DefaultMultiHashingFunction(final int maximumAllowed, final HashingFunctionAlgorithm algorithm, final String salt) {
        this(maximumAllowed, algorithm, salt.getBytes(StandardCharsets.UTF_8));
    }

    @Serial
    private void readObject(final ObjectInputStream objectInputStream)
            throws IOException, ClassNotFoundException {
        objectInputStream.defaultReadObject();
        hashCodeFunction = ThreadLocal.withInitial(() -> HASHING_FUNCTION_FACTORIES.get(algorithm).create(salt));
    }

    @Override
    public long hashCode(final int hashCode, final int hashFunctionIndex) {
        return hashCodeFunction.get().hashCode(hashCode, hashFunctionIndex);
    }
}
