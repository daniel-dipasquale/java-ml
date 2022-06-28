package com.dipasquale.search.mcts.simulation.concurrent;

import com.dipasquale.search.mcts.State;
import com.dipasquale.search.mcts.TraversalPolicy;
import com.dipasquale.search.mcts.concurrent.ConcurrentEdge;
import com.dipasquale.search.mcts.concurrent.ConcurrentSearchNode;
import com.dipasquale.search.mcts.expansion.concurrent.ExpansionLockContext;
import com.dipasquale.search.mcts.seek.ComprehensiveSeekPolicy;
import com.dipasquale.search.mcts.simulation.AbstractSimulationPolicy;

public final class ConcurrentSimulationPolicy<TAction, TEdge extends ConcurrentEdge, TState extends State<TAction, TState>> extends AbstractSimulationPolicy<TAction, TEdge, TState, ConcurrentSearchNode<TAction, TEdge, TState>, ExpansionLockContext<TAction, TEdge, TState>> {
    public ConcurrentSimulationPolicy(final ComprehensiveSeekPolicy comprehensiveSeekPolicy, final TraversalPolicy<TAction, TEdge, TState, ConcurrentSearchNode<TAction, TEdge, TState>> traversalPolicy) {
        super(comprehensiveSeekPolicy, traversalPolicy);
    }

    @Override
    protected ExpansionLockContext<TAction, TEdge, TState> createContext() {
        return new ExpansionLockContext<>();
    }

    @Override
    protected void visit(final ExpansionLockContext<TAction, TEdge, TState> context, final ConcurrentSearchNode<TAction, TEdge, TState> searchNode) {
        context.handOver(searchNode);
    }

    @Override
    protected ConcurrentSearchNode<TAction, TEdge, TState> select(final ExpansionLockContext<TAction, TEdge, TState> context, final ConcurrentSearchNode<TAction, TEdge, TState> searchNode) {
        if (!searchNode.getSimulationResultLock().tryLock()) {
            return null;
        }

        return super.select(context, searchNode);
    }

    @Override
    protected void cleanUp(final ExpansionLockContext<TAction, TEdge, TState> context) {
        context.release();
    }
}
