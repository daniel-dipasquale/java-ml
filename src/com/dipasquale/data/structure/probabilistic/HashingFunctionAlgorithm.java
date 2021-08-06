package com.dipasquale.data.structure.probabilistic;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum HashingFunctionAlgorithm {
    ADLER_32("ADLER-32"),
    CRC_32("CRC-32"),
    CRC_32C("CRC-32C"),
    MD5("MD5"), // message digest
    MURMUR_3_128("MURMUR-3-128"),
    MURMUR_3_32("MURMUR-3-32"),
    SHA_1("SHA-1"), // message digest
    SHA_256("SHA-256"), // message digest
    SHA_512("SHA-512"), // message digest
    SIP_HASH_24("SIP-HASH-24");

    private final String name;
}