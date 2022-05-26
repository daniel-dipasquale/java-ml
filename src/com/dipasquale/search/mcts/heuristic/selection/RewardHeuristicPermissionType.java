package com.dipasquale.search.mcts.heuristic.selection;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum RewardHeuristicPermissionType {
    ALLOWED_ON_INTENTIONAL_STATES,
    ALLOWED_ON_UNINTENTIONAL_STATES
}
