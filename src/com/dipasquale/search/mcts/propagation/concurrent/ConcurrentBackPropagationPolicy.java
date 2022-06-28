package com.dipasquale.search.mcts.propagation.concurrent;

import com.dipasquale.search.mcts.SearchNodeExplorer;
import com.dipasquale.search.mcts.State;
import com.dipasquale.search.mcts.concurrent.ConcurrentEdge;
import com.dipasquale.search.mcts.concurrent.ConcurrentSearchNode;
import com.dipasquale.search.mcts.propagation.AbstractBackPropagationPolicy;
import com.dipasquale.search.mcts.propagation.BackPropagationObserver;
import com.dipasquale.search.mcts.propagation.BackPropagationStep;

import java.util.concurrent.locks.Lock;

public final class ConcurrentBackPropagationPolicy<TAction, TEdge extends ConcurrentEdge, TState extends State<TAction, TState>, TContext> extends AbstractBackPropagationPolicy<TAction, TEdge, TState, ConcurrentSearchNode<TAction, TEdge, TState>, TContext> {
    public ConcurrentBackPropagationPolicy(final SearchNodeExplorer<TAction, TEdge, TState, ConcurrentSearchNode<TAction, TEdge, TState>> searchNodeExplorer, final BackPropagationStep<TAction, TEdge, TState, ConcurrentSearchNode<TAction, TEdge, TState>, TContext> step, final BackPropagationObserver<TAction, TState> observer) {
        super(searchNodeExplorer, step, observer);
    }

    private void process(final Lock lock, final ConcurrentSearchNode<TAction, TEdge, TState> rootSearchNode, final ConcurrentSearchNode<TAction, TEdge, TState> selectedSearchNode, final ConcurrentSearchNode<TAction, TEdge, TState> leafSearchNode) {
        lock.lock();

        try {
            super.process(rootSearchNode, selectedSearchNode, leafSearchNode);
        } finally {
            leafSearchNode.getSimulationResultLock().unlock();
            selectedSearchNode.getSelectionResultLock().unlock();
            lock.unlock();
        }
    }

    @Override
    public void process(final ConcurrentSearchNode<TAction, TEdge, TState> rootSearchNode, final ConcurrentSearchNode<TAction, TEdge, TState> selectedSearchNode, final ConcurrentSearchNode<TAction, TEdge, TState> leafSearchNode) {
        if (leafSearchNode != null) {
            process(rootSearchNode.getEdge().getLock().writeLock(), rootSearchNode, selectedSearchNode, leafSearchNode);
        } else if (selectedSearchNode != null) {
            selectedSearchNode.getSelectionResultLock().unlock();
        }
    }
}
