package com.dipasquale.search.mcts.simulation.concurrent;

import com.dipasquale.common.random.float1.RandomSupport;
import com.dipasquale.search.mcts.Action;
import com.dipasquale.search.mcts.State;
import com.dipasquale.search.mcts.concurrent.ConcurrentEdge;
import com.dipasquale.search.mcts.concurrent.ConcurrentSearchNode;
import com.dipasquale.search.mcts.simulation.AbstractIntentionalSimulationTraversalPolicy;

import java.util.concurrent.locks.Lock;

public final class ConcurrentIntentionalSimulationTraversalPolicy<TAction extends Action, TEdge extends ConcurrentEdge, TState extends State<TAction, TState>> extends AbstractIntentionalSimulationTraversalPolicy<TAction, TEdge, TState, ConcurrentSearchNode<TAction, TEdge, TState>> {
    public ConcurrentIntentionalSimulationTraversalPolicy(final RandomSupport randomSupport) {
        super(randomSupport);
    }

    private ConcurrentSearchNode<TAction, TEdge, TState> selectUnexplored(final Lock lock, final ConcurrentSearchNode<TAction, TEdge, TState> searchNode) {
        lock.lock();

        try {
            if (!searchNode.getUnexploredChildren().isEmpty()) {
                return super.selectUnexplored(searchNode);
            }
        } finally {
            lock.unlock();
        }

        if (searchNode.getExplorableChildren().isEmpty()) {
            return null;
        }

        return super.selectExplorable(searchNode);
    }

    @Override
    protected ConcurrentSearchNode<TAction, TEdge, TState> selectUnexplored(final ConcurrentSearchNode<TAction, TEdge, TState> searchNode) {
        return selectUnexplored(searchNode.getExpansionLock().writeLock(), searchNode);
    }

    @Override
    public ConcurrentSearchNode<TAction, TEdge, TState> next(final int simulations, final ConcurrentSearchNode<TAction, TEdge, TState> searchNode) {
        return super.next(simulations, searchNode);
    }
}
