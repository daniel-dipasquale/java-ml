package com.dipasquale.search.mcts.concurrent;

import com.dipasquale.search.mcts.Action;
import com.dipasquale.search.mcts.BackPropagationStep;
import com.dipasquale.search.mcts.Edge;
import com.dipasquale.search.mcts.State;
import lombok.RequiredArgsConstructor;

import java.util.concurrent.locks.Lock;

@RequiredArgsConstructor
public final class ConcurrentBackPropagationStep<TAction extends Action, TEdge extends Edge, TState extends State<TAction, TState>, TContext> implements BackPropagationStep<TAction, TEdge, TState, ConcurrentSearchNode<TAction, TEdge, TState>, TContext> {
    private final BackPropagationStep<TAction, TEdge, TState, ConcurrentSearchNode<TAction, TEdge, TState>, TContext> step;

    @Override
    public TContext createContext(final ConcurrentSearchNode<TAction, TEdge, TState> leafSearchNode) {
        return step.createContext(leafSearchNode);
    }

    private void process(final Lock lock, final TContext context, final ConcurrentSearchNode<TAction, TEdge, TState> currentSearchNode) {
        lock.lock();

        try {
            step.process(context, currentSearchNode);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void process(final TContext context, final ConcurrentSearchNode<TAction, TEdge, TState> currentSearchNode) {
        process(currentSearchNode.getBackPropagatingLock().writeLock(), context, currentSearchNode);
    }
}
