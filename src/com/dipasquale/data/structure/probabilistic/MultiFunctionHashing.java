package com.dipasquale.data.structure.probabilistic;

public interface MultiFunctionHashing {
    int getMaximumHashFunctions();

    long hashCode(int hashCode, int hashFunction);

    static MultiFunctionHashing createMd5(final int maximumHashFunctions, final String salt) {
        return new MultiFunctionHashingCrypto(maximumHashFunctions, MultiFunctionHashingCrypto.Algorithm.MD5, salt);
    }

    static MultiFunctionHashing createSha1(final int maximumHashFunctions, final String salt) {
        return new MultiFunctionHashingCrypto(maximumHashFunctions, MultiFunctionHashingCrypto.Algorithm.SHA_1, salt);
    }

    static MultiFunctionHashing createSha256(final int maximumHashFunctions, final String salt) {
        return new MultiFunctionHashingCrypto(maximumHashFunctions, MultiFunctionHashingCrypto.Algorithm.SHA_256, salt);
    }

    static MultiFunctionHashing createSha512(final int maximumHashFunctions, final String salt) {
        return new MultiFunctionHashingCrypto(maximumHashFunctions, MultiFunctionHashingCrypto.Algorithm.SHA_512, salt);
    }

    static MultiFunctionHashing createAdler32(final int maximumHashFunctions, final String salt) {
        return new MultiFunctionHashingGuava(maximumHashFunctions, MultiFunctionHashingGuava.Algorithm.ADLER_32, salt);
    }

    static MultiFunctionHashing createCrc32(final int maximumHashFunctions, final String salt) {
        return new MultiFunctionHashingGuava(maximumHashFunctions, MultiFunctionHashingGuava.Algorithm.CRC_32, salt);
    }

    static MultiFunctionHashing createCrc32c(final int maximumHashFunctions, final String salt) {
        return new MultiFunctionHashingGuava(maximumHashFunctions, MultiFunctionHashingGuava.Algorithm.CRC_32C, salt);
    }

    static MultiFunctionHashing createMurmur3_32(final int maximumHashFunctions, final String salt) {
        return new MultiFunctionHashingGuava(maximumHashFunctions, MultiFunctionHashingGuava.Algorithm.MURMUR_3_32, salt);
    }

    static MultiFunctionHashing createMurmur3_128(final int maximumHashFunctions, final String salt) {
        return new MultiFunctionHashingGuava(maximumHashFunctions, MultiFunctionHashingGuava.Algorithm.MURMUR_3_128, salt);
    }

    static MultiFunctionHashing createSipHash24(final int maximumHashFunctions, final String salt) {
        return new MultiFunctionHashingGuava(maximumHashFunctions, MultiFunctionHashingGuava.Algorithm.SIP_HASH_24, salt);
    }
}
