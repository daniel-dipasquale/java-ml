package com.dipasquale.data.structure.probabilistic;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum HashingFunctionAlgorithm {
    MD5("MD5"), // message digest
    SHA_1("SHA-1"), // message digest
    SHA_256("SHA-256"), // message digest
    SHA_512("SHA-512"); // message digest

    private final String name;
}