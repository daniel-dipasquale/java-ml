package com.dipasquale.search.mcts.propagation;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum BackPropagationType {
    IDENTITY,
    REVERSED_ON_BACKTRACK,
    REVERSED_ON_OPPONENT
}
