/*
 * java-ml
 * (c) 2021 daniel-dipasquale
 * released under the MIT license
 */

package com.dipasquale.data.structure.probabilistic;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serial;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

final class DefaultHashingFunction implements HashingFunction, Serializable {
    @Serial
    private static final long serialVersionUID = -2119483978497376865L;
    private final HashingFunctionAlgorithm algorithm;
    private final long offset;
    private final byte[] salt;
    private transient ThreadLocal<MessageDigest> messageDigestThreadLocal;

    DefaultHashingFunction(final HashingFunctionAlgorithm algorithm, final long offset, final byte[] salt) {
        this.algorithm = algorithm;
        this.offset = offset;
        this.salt = salt;
        this.messageDigestThreadLocal = ThreadLocal.withInitial(() -> createMessageDigest(algorithm.getName()));
    }

    private static MessageDigest createMessageDigest(final String algorithm) {
        try {
            return MessageDigest.getInstance(algorithm);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    @Serial
    private void readObject(final ObjectInputStream objectInputStream)
            throws IOException, ClassNotFoundException {
        objectInputStream.defaultReadObject();
        messageDigestThreadLocal = ThreadLocal.withInitial(() -> createMessageDigest(algorithm.getName()));
    }

    private byte[] createHashDigest(final byte[] input) {
        MessageDigest messageDigest = messageDigestThreadLocal.get();

        try {
            return messageDigest.digest(input);
        } finally {
            messageDigest.reset();
        }
    }

    @Override
    public long hashCode(final int itemHashCode, final int entropyId) {
        byte[] input = ByteBuffer.allocate(12 + salt.length)
                .putInt(itemHashCode)
                .putLong(offset - (long) entropyId * 1_024L)
                .put(salt)
                .array();

        byte[] output = createHashDigest(input);

        return ByteBuffer.wrap(output).getLong();
    }
}
