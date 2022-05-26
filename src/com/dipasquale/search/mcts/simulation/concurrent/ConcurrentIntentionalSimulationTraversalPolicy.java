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

    private ConcurrentSearchNode<TAction, TEdge, TState> selectUnexplored(final Lock lock, final ConcurrentSearchNode<TAction, TEdge, TState> currentSearchNode) {
        lock.lock();

        try {
            if (!currentSearchNode.getUnexploredChildren().isEmpty()) {
                return super.selectUnexplored(currentSearchNode);
            }
        } finally {
            lock.unlock();
        }

        if (currentSearchNode.getExplorableChildren().isEmpty()) {
            return null;
        }

        return super.selectExplorable(currentSearchNode);
    }

    @Override
    protected ConcurrentSearchNode<TAction, TEdge, TState> selectUnexplored(final ConcurrentSearchNode<TAction, TEdge, TState> currentSearchNode) {
        return selectUnexplored(currentSearchNode.getExpansionLock().writeLock(), currentSearchNode);
    }

    @Override
    public ConcurrentSearchNode<TAction, TEdge, TState> next(final int simulations, final ConcurrentSearchNode<TAction, TEdge, TState> currentSearchNode) {
        return super.next(simulations, currentSearchNode);
    }
}
