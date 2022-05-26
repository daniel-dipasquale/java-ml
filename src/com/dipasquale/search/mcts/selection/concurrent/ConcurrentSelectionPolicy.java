package com.dipasquale.search.mcts.selection.concurrent;

import com.dipasquale.search.mcts.Action;
import com.dipasquale.search.mcts.State;
import com.dipasquale.search.mcts.TraversalPolicy;
import com.dipasquale.search.mcts.concurrent.ConcurrentEdge;
import com.dipasquale.search.mcts.concurrent.ConcurrentSearchNode;
import com.dipasquale.search.mcts.expansion.ExpansionPolicy;
import com.dipasquale.search.mcts.expansion.concurrent.ExpansionLockContext;
import com.dipasquale.search.mcts.selection.AbstractSelectionPolicy;

import java.util.concurrent.locks.Lock;

public final class ConcurrentSelectionPolicy<TAction extends Action, TEdge extends ConcurrentEdge, TState extends State<TAction, TState>> extends AbstractSelectionPolicy<TAction, TEdge, TState, ConcurrentSearchNode<TAction, TEdge, TState>, ExpansionLockContext<TAction, TEdge, TState>> {
    public ConcurrentSelectionPolicy(final TraversalPolicy<TAction, TEdge, TState, ConcurrentSearchNode<TAction, TEdge, TState>> priorityTraversalPolicy, final TraversalPolicy<TAction, TEdge, TState, ConcurrentSearchNode<TAction, TEdge, TState>> subsequentTraversalPolicy, final ExpansionPolicy<TAction, TEdge, TState, ConcurrentSearchNode<TAction, TEdge, TState>> expansionPolicy) {
        super(priorityTraversalPolicy, subsequentTraversalPolicy, expansionPolicy);
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
    protected void selected(final ExpansionLockContext<TAction, TEdge, TState> context, final ConcurrentSearchNode<TAction, TEdge, TState> currentSearchNode) {
        currentSearchNode.getEdge().acquireSelection();
    }

    @Override
    protected void exit(final ExpansionLockContext<TAction, TEdge, TState> context) {
        context.exit();
    }

    private ConcurrentSearchNode<TAction, TEdge, TState> select(final Lock lock, final int simulations, final ConcurrentSearchNode<TAction, TEdge, TState> rootSearchNode) {
        lock.lock();

        try {
            return super.select(simulations, rootSearchNode);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public ConcurrentSearchNode<TAction, TEdge, TState> select(final int simulations, final ConcurrentSearchNode<TAction, TEdge, TState> rootSearchNode) {
        return select(rootSearchNode.getEdgeLock().readLock(), simulations, rootSearchNode);
    }
}
