package com.dipasquale.search.mcts.expansion.concurrent;

import com.dipasquale.search.mcts.Action;
import com.dipasquale.search.mcts.State;
import com.dipasquale.search.mcts.concurrent.ConcurrentEdge;
import com.dipasquale.search.mcts.concurrent.ConcurrentSearchNode;
import com.dipasquale.search.mcts.expansion.AbstractExpansionTraversalPolicy;
import com.dipasquale.search.mcts.expansion.ExpansionPolicy;

import java.util.concurrent.locks.Lock;

public final class ConcurrentExpansionTraversalPolicy<TAction extends Action, TEdge extends ConcurrentEdge, TState extends State<TAction, TState>> extends AbstractExpansionTraversalPolicy<TAction, TEdge, TState, ConcurrentSearchNode<TAction, TEdge, TState>> {
    public ConcurrentExpansionTraversalPolicy(final ExpansionPolicy<TAction, TEdge, TState, ConcurrentSearchNode<TAction, TEdge, TState>> expansionPolicy) {
        super(expansionPolicy);
    }

    private ConcurrentSearchNode<TAction, TEdge, TState> next(final Lock lock, final int simulations, final ConcurrentSearchNode<TAction, TEdge, TState> searchNode) {
        lock.lock();

        try {
            return super.next(simulations, searchNode);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public ConcurrentSearchNode<TAction, TEdge, TState> next(final int simulations, final ConcurrentSearchNode<TAction, TEdge, TState> searchNode) {
        if (searchNode.isExpanded()) {
            return null;
        }

        return next(searchNode.getExpansionLock().writeLock(), simulations, searchNode);
    }
}
