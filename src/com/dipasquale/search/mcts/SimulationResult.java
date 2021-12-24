package com.dipasquale.search.mcts;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@Getter
final class SimulationResult<T extends State> {
    private final SearchNode<T> searchNode;
    private final int statusId;
}
