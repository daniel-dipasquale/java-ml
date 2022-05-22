package com.dipasquale.search.mcts.common.concurrent;

import com.dipasquale.search.mcts.Action;
import com.dipasquale.search.mcts.Edge;
import com.dipasquale.search.mcts.ExpansionPolicy;
import com.dipasquale.search.mcts.State;
import com.dipasquale.search.mcts.concurrent.ConcurrentSearchNode;
import lombok.RequiredArgsConstructor;

import java.util.concurrent.locks.Lock;

@RequiredArgsConstructor
public final class ConcurrentExpansionPolicy<TAction extends Action, TEdge extends Edge, TState extends State<TAction, TState>> implements ExpansionPolicy<TAction, TEdge, TState, ConcurrentSearchNode<TAction, TEdge, TState>> {
    private final ExpansionPolicy<TAction, TEdge, TState, ConcurrentSearchNode<TAction, TEdge, TState>> expansionPolicy;
    private final boolean acquireWriteLock;

    private Lock getLock(final ConcurrentSearchNode<TAction, TEdge, TState> searchNode) {
        if (!acquireWriteLock) {
            return searchNode.getExpandingLock().readLock();
        }

        return searchNode.getExpandingLock().writeLock();
    }

    private void expand(final Lock lock, final ConcurrentSearchNode<TAction, TEdge, TState> searchNode) {
        lock.lock();

        try {
            expansionPolicy.expand(searchNode);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void expand(final ConcurrentSearchNode<TAction, TEdge, TState> searchNode) {
        expand(getLock(searchNode), searchNode);
    }
}
