package com.dipasquale.search.mcts.alphazero;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum PredictionBehaviorType {
    VALUE_FOR_INITIAL_STATE_IS_ZERO,
    INVERSE_VALUE_FOR_OPPONENT,
    INVERSE_POLICY_FOR_OPPONENT
}
