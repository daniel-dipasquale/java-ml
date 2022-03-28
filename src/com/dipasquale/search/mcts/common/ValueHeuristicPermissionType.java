package com.dipasquale.search.mcts.common;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum ValueHeuristicPermissionType {
    ALLOWED_ON_INTENTIONAL_STATES,
    ALLOWED_ON_UNINTENTIONAL_STATES
}
