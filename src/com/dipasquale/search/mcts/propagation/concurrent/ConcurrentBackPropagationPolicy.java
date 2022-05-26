package com.dipasquale.search.mcts.propagation.concurrent;

import com.dipasquale.search.mcts.Action;
import com.dipasquale.search.mcts.SearchNodeManager;
import com.dipasquale.search.mcts.State;
import com.dipasquale.search.mcts.concurrent.ConcurrentEdge;
import com.dipasquale.search.mcts.concurrent.ConcurrentSearchNode;
import com.dipasquale.search.mcts.propagation.AbstractBackPropagationPolicy;
import com.dipasquale.search.mcts.propagation.BackPropagationObserver;
import com.dipasquale.search.mcts.propagation.BackPropagationStep;

import java.util.concurrent.locks.Lock;

public final class ConcurrentBackPropagationPolicy<TAction extends Action, TEdge extends ConcurrentEdge, TState extends State<TAction, TState>, TContext> extends AbstractBackPropagationPolicy<TAction, TEdge, TState, ConcurrentSearchNode<TAction, TEdge, TState>, TContext> {
    public ConcurrentBackPropagationPolicy(final SearchNodeManager<TAction, TEdge, TState, ConcurrentSearchNode<TAction, TEdge, TState>> searchNodeManager, final BackPropagationStep<TAction, TEdge, TState, ConcurrentSearchNode<TAction, TEdge, TState>, TContext> step, final BackPropagationObserver<TAction, TState> observer) {
        super(searchNodeManager, step, observer);
    }

    private void process(final Lock lock, final ConcurrentSearchNode<TAction, TEdge, TState> rootSearchNode, final ConcurrentSearchNode<TAction, TEdge, TState> selectedSearchNode, final ConcurrentSearchNode<TAction, TEdge, TState> leafSearchNode) {
        lock.lock();

        try {
            super.process(rootSearchNode, selectedSearchNode, leafSearchNode);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void process(final ConcurrentSearchNode<TAction, TEdge, TState> rootSearchNode, final ConcurrentSearchNode<TAction, TEdge, TState> selectedSearchNode, final ConcurrentSearchNode<TAction, TEdge, TState> leafSearchNode) {
        process(rootSearchNode.getEdgeLock().writeLock(), rootSearchNode, selectedSearchNode, leafSearchNode);
    }
}
