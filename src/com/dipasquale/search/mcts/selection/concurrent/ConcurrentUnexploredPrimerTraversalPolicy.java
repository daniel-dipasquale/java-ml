package com.dipasquale.search.mcts.selection.concurrent;

import com.dipasquale.search.mcts.Action;
import com.dipasquale.search.mcts.State;
import com.dipasquale.search.mcts.concurrent.ConcurrentEdge;
import com.dipasquale.search.mcts.concurrent.ConcurrentSearchNode;
import com.dipasquale.search.mcts.selection.AbstractUnexploredPrimerTraversalPolicy;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.concurrent.locks.Lock;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class ConcurrentUnexploredPrimerTraversalPolicy<TAction extends Action, TEdge extends ConcurrentEdge, TState extends State<TAction, TState>> extends AbstractUnexploredPrimerTraversalPolicy<TAction, TEdge, TState, ConcurrentSearchNode<TAction, TEdge, TState>> {
    private static final ConcurrentUnexploredPrimerTraversalPolicy<?, ?, ?> INSTANCE = new ConcurrentUnexploredPrimerTraversalPolicy<>();

    public static <TAction extends Action, TEdge extends ConcurrentEdge, TState extends State<TAction, TState>> ConcurrentUnexploredPrimerTraversalPolicy<TAction, TEdge, TState> getInstance() {
        return (ConcurrentUnexploredPrimerTraversalPolicy<TAction, TEdge, TState>) INSTANCE;
    }

    private ConcurrentSearchNode<TAction, TEdge, TState> selectUnexplored(final Lock lock, final ConcurrentSearchNode<TAction, TEdge, TState> currentSearchNode) {
        lock.lock();

        try {
            if (currentSearchNode.getUnexploredChildren().isEmpty()) {
                return null;
            }

            return super.selectUnexplored(currentSearchNode);
        } finally {
            lock.unlock();
        }
    }

    @Override
    protected ConcurrentSearchNode<TAction, TEdge, TState> selectUnexplored(final ConcurrentSearchNode<TAction, TEdge, TState> currentSearchNode) {
        return selectUnexplored(currentSearchNode.getExpansionLock().writeLock(), currentSearchNode);
    }
}
