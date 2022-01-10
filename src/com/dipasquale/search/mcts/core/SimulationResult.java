package com.dipasquale.search.mcts.core;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@Getter
final class SimulationResult<TState extends State, TEdge extends Edge, TEnvironment extends Environment<TState, TEnvironment>> {
    private final SearchNode<TState, TEdge, TEnvironment> node;
    private final int statusId;
}