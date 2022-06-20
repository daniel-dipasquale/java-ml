package com.dipasquale.search.mcts.concurrent;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum EdgeTraversalLockType {
    SHARED,
    RCU
}
