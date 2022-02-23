package com.dipasquale.simulation.mcts.alphazero;

import com.dipasquale.search.mcts.alphazero.AlphaZeroEdge;
import com.dipasquale.search.mcts.core.Action;
import com.dipasquale.search.mcts.core.EdgeFactory;
import com.dipasquale.search.mcts.core.SearchNode;
import com.dipasquale.search.mcts.core.State;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@Getter
public final class NeatAlphaZeroHeuristicContext<TAction extends Action, TState extends State<TAction, TState>> {
    private final SearchNode<TAction, AlphaZeroEdge, TState> node;
    private final EdgeFactory<AlphaZeroEdge> edgeFactory;
}
