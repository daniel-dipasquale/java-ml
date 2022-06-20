package com.dipasquale.search.mcts.concurrent;

import com.dipasquale.search.mcts.Action;
import com.dipasquale.search.mcts.SearchNodeFactory;
import com.dipasquale.search.mcts.State;

import java.util.List;

@FunctionalInterface
public interface ConcurrentSearchNodeFactorySelector<TAction extends Action, TEdge extends ConcurrentEdge, TState extends State<TAction, TState>> {
    SearchNodeFactory<TAction, TEdge, TState, ConcurrentSearchNode<TAction, TEdge, TState>> select(EdgeTraversalLockType edgeTraversalLockType, List<Long> threadIds);
}
