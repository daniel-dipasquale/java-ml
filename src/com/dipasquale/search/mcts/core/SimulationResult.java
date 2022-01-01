package com.dipasquale.search.mcts.core;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@Getter
final class SimulationResult<TState extends SearchState, TEdge extends SearchEdge> {
    private final SearchNode<TState, TEdge> node;
    private final int statusId;
}
