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
    public ConcurrentSelectionPolicy(final TraversalPolicy<TAction, TEdge, TState, ConcurrentSearchNode<TAction, TEdge, TState>> unexploredPrimerTraversalPolicy, final TraversalPolicy<TAction, TEdge, TState, ConcurrentSearchNode<TAction, TEdge, TState>> explorableTraversalPolicy, final ExpansionPolicy<TAction, TEdge, TState, ConcurrentSearchNode<TAction, TEdge, TState>> expansionPolicy) {
        super(unexploredPrimerTraversalPolicy, explorableTraversalPolicy, expansionPolicy);
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
    protected boolean shouldSelect(final ConcurrentSearchNode<TAction, TEdge, TState> searchNode) {
        return super.shouldSelect(searchNode) && searchNode.getSelectionResultLock().tryLock();
    }

    @Override
    protected ConcurrentSearchNode<TAction, TEdge, TState> selectLeaf(final ConcurrentSearchNode<TAction, TEdge, TState> searchNode) {
        if (!searchNode.getSelectionResultLock().tryLock()) {
            return null;
        }

        return super.selectLeaf(searchNode);
    }

    @Override
    protected void cleanUp(final ExpansionLockContext<TAction, TEdge, TState> context) {
        context.release();
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
        return select(rootSearchNode.getEdge().getLock().readLock(), simulations, rootSearchNode);
    }
}
