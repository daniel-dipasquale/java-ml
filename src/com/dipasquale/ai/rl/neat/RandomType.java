package com.dipasquale.ai.rl.neat;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum RandomType {
    UNIFORM,
    BELL_CURVE,
    QUADRUPLE_SIGMOID,
    QUADRUPLE_STEEPENED_SIGMOID
}
