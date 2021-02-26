package com.dipasquale.data.structure.probabilistic;

import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.nio.charset.StandardCharsets;
import java.util.EnumMap;

final class MultiFunctionHashingGuava implements MultiFunctionHashing {
    private static final long OFFSET = 2_211_653_919_168_766_835L;
    private static final int SEED = MultiFunctionHashingGuava.class.hashCode();
    private static final EnumMap<Algorithm, HashFunction> HASH_FUNCTIONS = createHashFunctions();
    @Getter
    private final int maximumHashFunctions;
    private final ThreadLocal<HashFunction> hashFunctionFactory;
    private final byte[] salt;

    MultiFunctionHashingGuava(final int maximumHashFunctions, final Algorithm algorithm, final String salt) {
        this.maximumHashFunctions = maximumHashFunctions;
        this.hashFunctionFactory = ThreadLocal.withInitial(() -> HASH_FUNCTIONS.get(algorithm));
        this.salt = salt.getBytes(StandardCharsets.UTF_8);
    }

    private static EnumMap<Algorithm, HashFunction> createHashFunctions() {
        EnumMap<Algorithm, HashFunction> hashFunctions = new EnumMap<>(Algorithm.class);

        hashFunctions.put(Algorithm.ADLER_32, Hashing.adler32());
        hashFunctions.put(Algorithm.CRC_32, Hashing.crc32());
        hashFunctions.put(Algorithm.CRC_32C, Hashing.crc32c());
        hashFunctions.put(Algorithm.MURMUR_3_32, Hashing.murmur3_32(SEED));
        hashFunctions.put(Algorithm.MURMUR_3_128, Hashing.murmur3_128(SEED));
        hashFunctions.put(Algorithm.SIP_HASH_24, Hashing.sipHash24());

        return hashFunctions;
    }

    @Override
    public long hashCode(final int hashCode, final int hashFunction) {
        return hashFunctionFactory.get().newHasher()
                .putInt(hashCode)
                .putLong(OFFSET - (long) hashFunction * 4_213)
                .putBytes(salt)
                .hash()
                .asLong();
    }

    @RequiredArgsConstructor
    public enum Algorithm {
        ADLER_32("ADLER-32"),
        CRC_32("CRC-32"),
        CRC_32C("CRC-32C"),
        MURMUR_3_32("MURMUR-3-32"),
        MURMUR_3_128("MURMUR-3-128"),
        SIP_HASH_24("SIP-HASH-24");

        private final String value;
    }
}
