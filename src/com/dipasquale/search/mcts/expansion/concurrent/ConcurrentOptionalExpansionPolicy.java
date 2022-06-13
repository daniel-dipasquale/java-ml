package com.dipasquale.search.mcts.expansion.concurrent;

import com.dipasquale.search.mcts.Action;
import com.dipasquale.search.mcts.State;
import com.dipasquale.search.mcts.concurrent.ConcurrentEdge;
import com.dipasquale.search.mcts.concurrent.ConcurrentSearchNode;
import com.dipasquale.search.mcts.expansion.ExpansionPolicy;
import lombok.RequiredArgsConstructor;

import java.util.concurrent.locks.Lock;

@RequiredArgsConstructor
public final class ConcurrentOptionalExpansionPolicy<TAction extends Action, TEdge extends ConcurrentEdge, TState extends State<TAction, TState>> implements ExpansionPolicy<TAction, TEdge, TState, ConcurrentSearchNode<TAction, TEdge, TState>> {
    private final ExpansionPolicy<TAction, TEdge, TState, ConcurrentSearchNode<TAction, TEdge, TState>> expansionPolicy;

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
        if (!searchNode.isExpanded()) {
            expand(searchNode.getExpansionLock().writeLock(), searchNode);
        }
    }
}
