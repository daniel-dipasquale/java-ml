package com.dipasquale.search.mcts.selection.concurrent;

import com.dipasquale.search.mcts.State;
import com.dipasquale.search.mcts.concurrent.ConcurrentEdge;
import com.dipasquale.search.mcts.concurrent.ConcurrentSearchNode;
import com.dipasquale.search.mcts.expansion.ExpansionPolicy;
import com.dipasquale.search.mcts.selection.AbstractUnexploredPrimerTraversalPolicy;

import java.util.concurrent.locks.Lock;

public final class ConcurrentUnexploredPrimerTraversalPolicy<TAction, TEdge extends ConcurrentEdge, TState extends State<TAction, TState>> extends AbstractUnexploredPrimerTraversalPolicy<TAction, TEdge, TState, ConcurrentSearchNode<TAction, TEdge, TState>> {
    public ConcurrentUnexploredPrimerTraversalPolicy(final ExpansionPolicy<TAction, TEdge, TState, ConcurrentSearchNode<TAction, TEdge, TState>> expansionPolicy) {
        super(expansionPolicy);
    }

    private ConcurrentSearchNode<TAction, TEdge, TState> selectUnexplored(final Lock lock, final ConcurrentSearchNode<TAction, TEdge, TState> searchNode) {
        lock.lock();

        try {
            if (searchNode.getUnexploredChildren().isEmpty()) {
                return null;
            }

            return super.selectUnexplored(searchNode);
        } finally {
            lock.unlock();
        }
    }

    @Override
    protected ConcurrentSearchNode<TAction, TEdge, TState> selectUnexplored(final ConcurrentSearchNode<TAction, TEdge, TState> searchNode) {
        return selectUnexplored(searchNode.getExpansionLock().writeLock(), searchNode);
    }
}
