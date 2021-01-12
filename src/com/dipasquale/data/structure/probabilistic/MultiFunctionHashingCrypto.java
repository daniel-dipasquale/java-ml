package com.dipasquale.data.structure.probabilistic;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

final class MultiFunctionHashingCrypto implements MultiFunctionHashing {
    private static final long OFFSET = 3_847_382_443_595_522_248L;
    @Getter
    private final int maximumHashFunctions;
    private final ThreadLocal<MessageDigest> messageDigestThreadLocal;
    private final byte[] salt;

    MultiFunctionHashingCrypto(final int maximumHashFunctions, final Algorithm algorithm, final String salt) {
        this.maximumHashFunctions = maximumHashFunctions;
        this.messageDigestThreadLocal = ThreadLocal.withInitial(() -> getMessageDigest(algorithm.value));
        this.salt = salt.getBytes(StandardCharsets.UTF_8);
    }

    private static MessageDigest getMessageDigest(final String algorithm) {
        try {
            return MessageDigest.getInstance(algorithm);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public byte[] getCryptoHash(final byte[] input) {
        MessageDigest messageDigest = messageDigestThreadLocal.get();

        try {
            return messageDigest.digest(input);
        } finally {
            messageDigest.reset();
        }
    }

    @Override
    public long hashCode(final int hashCode, final int hashFunction) {
        byte[] input = ByteBuffer.allocate(12 + salt.length)
                .putInt(hashCode)
                .putLong(OFFSET - (long) hashFunction * 1_024L)
                .put(salt)
                .array();

        byte[] output = getCryptoHash(input);

        return ByteBuffer.wrap(output).getLong();
    }

    @RequiredArgsConstructor
    public enum Algorithm {
        MD5("MD5"),
        SHA_1("SHA-1"),
        SHA_256("SHA-256"),
        SHA_512("SHA-512");

        private final String value;
    }
}
