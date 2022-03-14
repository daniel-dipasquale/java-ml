package com.dipasquale.search.mcts.alphazero;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum BackPropagationType {
    IDENTITY,
    REVERSED_ON_BACKTRACK,
    REVERSED_ON_OPPONENT
}
