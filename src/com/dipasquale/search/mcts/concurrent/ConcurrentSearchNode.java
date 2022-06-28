package com.dipasquale.search.mcts.concurrent;

import com.dipasquale.search.mcts.SearchNode;
import com.dipasquale.search.mcts.State;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;

public interface ConcurrentSearchNode<TAction, TEdge extends ConcurrentEdge, TState extends State<TAction, TState>> extends SearchNode<TAction, TEdge, TState, ConcurrentSearchNode<TAction, TEdge, TState>> {
    Lock getSelectionResultLock();

    ReadWriteLock getExpansionLock();

    Lock getSimulationResultLock();
}
