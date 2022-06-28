package com.dipasquale.search.mcts.expansion.concurrent;

import com.dipasquale.search.mcts.State;
import com.dipasquale.search.mcts.concurrent.ConcurrentEdge;
import com.dipasquale.search.mcts.concurrent.ConcurrentSearchNode;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class ExpansionLockContext<TAction, TEdge extends ConcurrentEdge, TState extends State<TAction, TState>> {
    private ConcurrentSearchNode<TAction, TEdge, TState> previousSearchNode = null;

    private static <TAction, TEdge extends ConcurrentEdge, TState extends State<TAction, TState>> void lock(final ConcurrentSearchNode<TAction, TEdge, TState> searchNode) {
        searchNode.getExpansionLock().readLock().lock();
    }

    private static <TAction, TEdge extends ConcurrentEdge, TState extends State<TAction, TState>> void unlock(final ConcurrentSearchNode<TAction, TEdge, TState> searchNode) {
        searchNode.getExpansionLock().readLock().unlock();
    }

    public void handOver(final ConcurrentSearchNode<TAction, TEdge, TState> searchNode) {
        if (previousSearchNode != null) {
            unlock(previousSearchNode);
        }

        previousSearchNode = searchNode;
        lock(previousSearchNode);
    }

    public void release() {
        if (previousSearchNode == null) {
            return;
        }

        unlock(previousSearchNode);
        previousSearchNode = null;
    }
}
