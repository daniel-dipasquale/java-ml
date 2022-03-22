package com.dipasquale.search.mcts.alphazero;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum PredictionBehaviorType {
    VALUE_HEURISTIC_ALLOWED_ON_INTENTIONAL_STATES,
    VALUE_REVERSED_ON_OPPONENT,
    POLICY_REVERSED_ON_OPPONENT
}
