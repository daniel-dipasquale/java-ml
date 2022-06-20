package com.dipasquale.search.mcts.concurrent;

import com.dipasquale.search.mcts.AbstractSearchNodeExplorer;
import com.dipasquale.search.mcts.Action;
import com.dipasquale.search.mcts.State;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.concurrent.locks.Lock;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class ConcurrentSearchNodeExplorer<TAction extends Action, TEdge extends ConcurrentEdge, TState extends State<TAction, TState>> extends AbstractSearchNodeExplorer<TAction, TEdge, TState, ConcurrentSearchNode<TAction, TEdge, TState>> {
    private static final ConcurrentSearchNodeExplorer<?, ?, ?> INSTANCE = new ConcurrentSearchNodeExplorer<>();

    public static <TAction extends Action, TEdge extends ConcurrentEdge, TState extends State<TAction, TState>> ConcurrentSearchNodeExplorer<TAction, TEdge, TState> getInstance() {
        return (ConcurrentSearchNodeExplorer<TAction, TEdge, TState>) INSTANCE;
    }

    private boolean isFullyExplored(final Lock lock, final ConcurrentSearchNode<TAction, TEdge, TState> searchNode) {
        lock.lock();

        try {
            return super.isFullyExplored(searchNode);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean isFullyExplored(final ConcurrentSearchNode<TAction, TEdge, TState> searchNode) {
        return isFullyExplored(searchNode.getExpansionLock().readLock(), searchNode);
    }

    private boolean declareFullyExplored(final Lock lock, final ConcurrentSearchNode<TAction, TEdge, TState> searchNode) {
        lock.lock();

        try {
            return super.declareFullyExplored(searchNode);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean declareFullyExplored(final ConcurrentSearchNode<TAction, TEdge, TState> searchNode) {
        return declareFullyExplored(searchNode.getExpansionLock().writeLock(), searchNode);
    }
}
