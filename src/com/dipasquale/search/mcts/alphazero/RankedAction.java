package com.dipasquale.search.mcts.alphazero;

import com.dipasquale.search.mcts.Action;
import com.dipasquale.search.mcts.SearchNode;
import com.dipasquale.search.mcts.State;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@Getter
final class RankedAction<TAction extends Action, TState extends State<TAction, TState>> {
    private final SearchNode<TAction, AlphaZeroEdge, TState> node;
    private final float efficiency;
}
