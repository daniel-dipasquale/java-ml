package com.dipasquale.data.structure.probabilistic;

import com.google.common.hash.HashFunction;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class GuavaHashingFunction implements HashingFunction, Serializable {
    @Serial
    private static final long serialVersionUID = 8381418950104918085L;
    private final HashFunction hashFunction;
    private final long offset;
    private final byte[] salt;

    @Override
    public long hashCode(final int itemHashCode, final int entropyId) {
        return hashFunction.newHasher()
                .putInt(itemHashCode)
                .putLong(offset - (long) entropyId * 4_213L)
                .putBytes(salt)
                .hash()
                .asLong();
    }
}
