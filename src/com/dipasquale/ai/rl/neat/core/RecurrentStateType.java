package com.dipasquale.ai.rl.neat.core;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public enum RecurrentStateType {
    DEFAULT(1),
    LSTM(5),
    GRU(4);

    private final int modifiers;
}
