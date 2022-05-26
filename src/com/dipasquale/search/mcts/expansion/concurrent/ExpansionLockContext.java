package com.dipasquale.search.mcts.expansion.concurrent;

import com.dipasquale.search.mcts.Action;
import com.dipasquale.search.mcts.State;
import com.dipasquale.search.mcts.concurrent.ConcurrentEdge;
import com.dipasquale.search.mcts.concurrent.ConcurrentSearchNode;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class ExpansionLockContext<TAction extends Action, TEdge extends ConcurrentEdge, TState extends State<TAction, TState>> {
    private ConcurrentSearchNode<TAction, TEdge, TState> previousSearchNode = null;

    public void visit(final ConcurrentSearchNode<TAction, TEdge, TState> currentSearchNode) {
        if (previousSearchNode != null) {
            previousSearchNode.getExpansionLock().readLock().unlock();
        }

        currentSearchNode.getExpansionLock().readLock().lock();
        previousSearchNode = currentSearchNode;
    }

    public void exit() {
        if (previousSearchNode != null) {
            previousSearchNode.getExpansionLock().readLock().unlock();
            previousSearchNode = null;
        }
    }
}
