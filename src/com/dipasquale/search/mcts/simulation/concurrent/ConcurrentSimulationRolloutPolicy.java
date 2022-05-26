package com.dipasquale.search.mcts.simulation.concurrent;

import com.dipasquale.search.mcts.Action;
import com.dipasquale.search.mcts.State;
import com.dipasquale.search.mcts.TraversalPolicy;
import com.dipasquale.search.mcts.concurrent.ConcurrentEdge;
import com.dipasquale.search.mcts.concurrent.ConcurrentSearchNode;
import com.dipasquale.search.mcts.expansion.ExpansionPolicy;
import com.dipasquale.search.mcts.expansion.concurrent.ExpansionLockContext;
import com.dipasquale.search.mcts.seek.FullSeekPolicy;
import com.dipasquale.search.mcts.simulation.AbstractSimulationRolloutPolicy;

public final class ConcurrentSimulationRolloutPolicy<TAction extends Action, TEdge extends ConcurrentEdge, TState extends State<TAction, TState>> extends AbstractSimulationRolloutPolicy<TAction, TEdge, TState, ConcurrentSearchNode<TAction, TEdge, TState>, ExpansionLockContext<TAction, TEdge, TState>> {
    public ConcurrentSimulationRolloutPolicy(final FullSeekPolicy searchPolicy, final TraversalPolicy<TAction, TEdge, TState, ConcurrentSearchNode<TAction, TEdge, TState>> traversalPolicy, final ExpansionPolicy<TAction, TEdge, TState, ConcurrentSearchNode<TAction, TEdge, TState>> expansionPolicy) {
        super(searchPolicy, traversalPolicy, expansionPolicy);
    }

    @Override
    protected ExpansionLockContext<TAction, TEdge, TState> createContext() {
        return new ExpansionLockContext<>();
    }

    @Override
    protected void visit(final ExpansionLockContext<TAction, TEdge, TState> context, final ConcurrentSearchNode<TAction, TEdge, TState> currentSearchNode) {
        context.visit(currentSearchNode);
    }

    @Override
    protected boolean selected(final ExpansionLockContext<TAction, TEdge, TState> context, final ConcurrentSearchNode<TAction, TEdge, TState> currentSearchNode) {
        return currentSearchNode.getLeafLock().tryLock();
    }

    @Override
    protected void exit(final ExpansionLockContext<TAction, TEdge, TState> context) {
        context.exit();
    }
}
