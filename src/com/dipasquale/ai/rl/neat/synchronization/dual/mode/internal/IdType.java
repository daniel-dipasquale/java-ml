package com.dipasquale.ai.rl.neat.synchronization.dual.mode.internal;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum IdType {
    INPUT_NODE("n1i"),
    OUTPUT_NODE("n4h"),
    BIAS_NODE("n2b"),
    HIDDEN_NODE("n3h"),
    INNOVATION_ID("i"),
    GENOME("g"),
    SPECIES("s");

    private final String name;
}
