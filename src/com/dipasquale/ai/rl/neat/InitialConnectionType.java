package com.dipasquale.ai.rl.neat;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum InitialConnectionType {
    FULLY_CONNECTED,
    FULL_CONNECTED_EXCLUDING_BIAS
}
