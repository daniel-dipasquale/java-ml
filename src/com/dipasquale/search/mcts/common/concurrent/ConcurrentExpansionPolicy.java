package com.dipasquale.search.mcts.common.concurrent;

import com.dipasquale.search.mcts.Action;
import com.dipasquale.search.mcts.Edge;
import com.dipasquale.search.mcts.ExpansionPolicy;
import com.dipasquale.search.mcts.State;
import com.dipasquale.search.mcts.concurrent.ConcurrentSearchNode;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class ConcurrentExpansionPolicy<TAction extends Action, TEdge extends Edge, TState extends State<TAction, TState>> implements ExpansionPolicy<TAction, TEdge, TState, ConcurrentSearchNode<TAction, TEdge, TState>> {
    private final ExpansionPolicy<TAction, TEdge, TState, ConcurrentSearchNode<TAction, TEdge, TState>> expansionPolicy;

    @Override
    public void expand(final ConcurrentSearchNode<TAction, TEdge, TState> searchNode) {
        searchNode.getExpandingLock().writeLock().lock();

        try {
            expansionPolicy.expand(searchNode);
        } finally {
            searchNode.getExpandingLock().writeLock().unlock();
        }
    }
}
