package com.dipasquale.data.structure.probabilistic;

import com.google.common.hash.HashFunction;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class GuavaHashingFunction implements HashingFunction, Serializable {
    private final HashFunction hashFunction;
    private final long offset;
    private final byte[] salt;

    @Override
    public long hashCode(final int hashCode, final int hashFunctionIndex) {
        return hashFunction.newHasher()
                .putInt(hashCode)
                .putLong(offset - (long) hashFunctionIndex * 4_213L)
                .putBytes(salt)
                .hash()
                .asLong();
    }
}
