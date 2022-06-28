package com.dipasquale.search.mcts.concurrent;

import com.dipasquale.search.mcts.SearchNodeFactory;
import com.dipasquale.search.mcts.State;

import java.util.Set;

@FunctionalInterface
public interface ConcurrentSearchNodeFactorySelector<TAction, TEdge extends ConcurrentEdge, TState extends State<TAction, TState>> {
    SearchNodeFactory<TAction, TEdge, TState, ConcurrentSearchNode<TAction, TEdge, TState>> select(EdgeTraversalLockType edgeTraversalLockType, Set<Long> threadIds);
}
