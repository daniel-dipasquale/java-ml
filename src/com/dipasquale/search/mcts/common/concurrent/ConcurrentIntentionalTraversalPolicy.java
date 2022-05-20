package com.dipasquale.search.mcts.common.concurrent;

import com.dipasquale.common.OptimalPairSelector;
import com.dipasquale.search.mcts.Action;
import com.dipasquale.search.mcts.Edge;
import com.dipasquale.search.mcts.SelectionConfidenceCalculator;
import com.dipasquale.search.mcts.State;
import com.dipasquale.search.mcts.common.AbstractIntentionalTraversalPolicy;
import com.dipasquale.search.mcts.concurrent.ConcurrentSearchNode;
import com.dipasquale.synchronization.InterruptedRuntimeException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;

public final class ConcurrentIntentionalTraversalPolicy<TAction extends Action, TEdge extends Edge, TState extends State<TAction, TState>> extends AbstractIntentionalTraversalPolicy<TAction, TEdge, TState, ConcurrentSearchNode<TAction, TEdge, TState>, ConcurrentIntentionalTraversalPolicy.Context<TAction, TEdge, TState>> {
    public ConcurrentIntentionalTraversalPolicy(final SelectionConfidenceCalculator<TEdge> selectionConfidenceCalculator) {
        super(selectionConfidenceCalculator);
    }

    @Override
    protected ConcurrentIntentionalTraversalPolicy.Context<TAction, TEdge, TState> createContext() {
        return new Context<>();
    }

    @Override
    protected void collectConfidenceCalculation(final ConcurrentIntentionalTraversalPolicy.Context<TAction, TEdge, TState> context, final ConcurrentSearchNode<TAction, TEdge, TState> childSearchNode, final TEdge parentEdge, final OptimalPairSelector<Float, Integer> optimalChildIndexSelector, final int index) {
        context.childSearchNodeIndexes.put(childSearchNode, index);

        if (childSearchNode.getBackPropagatingLock().readLock().tryLock()) {
            try {
                super.collectConfidenceCalculation(context, childSearchNode, parentEdge, optimalChildIndexSelector, index);
            } finally {
                childSearchNode.getBackPropagatingLock().readLock().unlock();
            }
        }
    }

    @Override
    protected int getOptimalIndex(final ConcurrentIntentionalTraversalPolicy.Context<TAction, TEdge, TState> context, final ConcurrentSearchNode<TAction, TEdge, TState> parentSearchNode, final OptimalPairSelector<Float, Integer> optimalChildIndexSelector) {
        while (optimalChildIndexSelector.getReplacedCount() == 0) {
            TEdge parentEdge = parentSearchNode.getEdge();

            try {
                ReadWriteLock readWriteLock = parentSearchNode.getBackPropagatingLockRing().awaitAnyUnlocked();

                if (readWriteLock.readLock().tryLock()) {
                    try {
                        ConcurrentSearchNode<TAction, TEdge, TState> childSearchNode = parentSearchNode.getBackPropagatingLockRing().identify(readWriteLock);
                        int index = context.childSearchNodeIndexes.get(childSearchNode);

                        super.collectConfidenceCalculation(context, childSearchNode, parentEdge, optimalChildIndexSelector, index);

                        return super.getOptimalIndex(context, parentSearchNode, optimalChildIndexSelector);
                    } finally {
                        readWriteLock.readLock().unlock();
                    }
                }
            } catch (InterruptedException e) {
                throw new InterruptedRuntimeException("unable to get the optimal index", e);
            }
        }

        return super.getOptimalIndex(context, parentSearchNode, optimalChildIndexSelector);
    }

    @Override
    public ConcurrentSearchNode<TAction, TEdge, TState> next(final int simulations, final ConcurrentSearchNode<TAction, TEdge, TState> searchNode) {
        searchNode.getExpandingLock().readLock().lock();

        try {
            return super.next(simulations, searchNode);
        } finally {
            searchNode.getExpandingLock().readLock().unlock();
        }
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class Context<TAction extends Action, TEdge extends Edge, TState extends State<TAction, TState>> {
        private final Map<ConcurrentSearchNode<TAction, TEdge, TState>, Integer> childSearchNodeIndexes = new IdentityHashMap<>();
    }
}
