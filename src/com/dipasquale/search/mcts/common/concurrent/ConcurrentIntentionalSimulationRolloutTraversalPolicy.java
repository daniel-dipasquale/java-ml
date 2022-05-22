package com.dipasquale.search.mcts.common.concurrent;

import com.dipasquale.common.random.float1.RandomSupport;
import com.dipasquale.search.mcts.Action;
import com.dipasquale.search.mcts.Edge;
import com.dipasquale.search.mcts.State;
import com.dipasquale.search.mcts.common.AbstractIntentionalSimulationRolloutTraversalPolicy;
import com.dipasquale.search.mcts.concurrent.ConcurrentSearchNode;

import java.util.concurrent.locks.Lock;

public final class ConcurrentIntentionalSimulationRolloutTraversalPolicy<TAction extends Action, TEdge extends Edge, TState extends State<TAction, TState>> extends AbstractIntentionalSimulationRolloutTraversalPolicy<TAction, TEdge, TState, ConcurrentSearchNode<TAction, TEdge, TState>> {
    public ConcurrentIntentionalSimulationRolloutTraversalPolicy(final RandomSupport randomSupport) {
        super(randomSupport);
    }

    private ConcurrentSearchNode<TAction, TEdge, TState> selectUnexplored(final Lock lock, final ConcurrentSearchNode<TAction, TEdge, TState> searchNode) {
        lock.lock();

        try {
            return super.selectUnexplored(searchNode);
        } finally {
            lock.unlock();
        }
    }

    @Override
    protected ConcurrentSearchNode<TAction, TEdge, TState> selectUnexplored(final ConcurrentSearchNode<TAction, TEdge, TState> searchNode) {
        return selectUnexplored(searchNode.getExpandingLock().writeLock(), searchNode);
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
        return next(searchNode.getExpandingLock().readLock(), simulations, searchNode);
    }
}
