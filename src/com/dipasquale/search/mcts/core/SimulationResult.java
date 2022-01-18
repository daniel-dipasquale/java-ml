package com.dipasquale.search.mcts.core;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@Getter
final class SimulationResult<TAction extends Action, TEdge extends Edge, TState extends State<TAction, TState>> {
    private final SearchNode<TAction, TEdge, TState> node;
    private final int statusId;
}
