package com.dipasquale.ai.rl.neat;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum InitialConnectionType {
    ALL_INPUTS_AND_BIASES_TO_ALL_OUTPUTS,
    ALL_INPUTS_TO_ALL_OUTPUTS,
    RANDOM
}
