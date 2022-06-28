package com.dipasquale.search.mcts.concurrent;

import com.dipasquale.search.mcts.AbstractSearchNodeExplorer;
import com.dipasquale.search.mcts.State;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.concurrent.locks.Lock;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class ConcurrentSearchNodeExplorer<TAction, TEdge extends ConcurrentEdge, TState extends State<TAction, TState>> extends AbstractSearchNodeExplorer<TAction, TEdge, TState, ConcurrentSearchNode<TAction, TEdge, TState>> {
    private static final ConcurrentSearchNodeExplorer<?, ?, ?> INSTANCE = new ConcurrentSearchNodeExplorer<>();

    public static <TAction, TEdge extends ConcurrentEdge, TState extends State<TAction, TState>> ConcurrentSearchNodeExplorer<TAction, TEdge, TState> getInstance() {
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

    private boolean notifyParentIsFullyExplored(final Lock lock, final ConcurrentSearchNode<TAction, TEdge, TState> searchNode) {
        lock.lock();

        try {
            return super.notifyParentIsFullyExplored(searchNode);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean notifyParentIsFullyExplored(final ConcurrentSearchNode<TAction, TEdge, TState> searchNode) {
        return notifyParentIsFullyExplored(searchNode.getParent().getExpansionLock().writeLock(), searchNode);
    }
}
