package com.dipasquale.ai.rl.neat;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public enum IdType {
    INPUT_NODE("n1i"),
    OUTPUT_NODE("n4o"),
    BIAS_NODE("n2b"),
    HIDDEN_NODE("n3h"),
    INNOVATION_ID("i"),
    SPECIES("s");

    private final String name;
}
