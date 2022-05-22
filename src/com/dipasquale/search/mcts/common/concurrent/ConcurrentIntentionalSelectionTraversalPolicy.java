package com.dipasquale.search.mcts.common.concurrent;

import com.dipasquale.common.OptimalPairSelector;
import com.dipasquale.search.mcts.Action;
import com.dipasquale.search.mcts.Edge;
import com.dipasquale.search.mcts.SelectionConfidenceCalculator;
import com.dipasquale.search.mcts.State;
import com.dipasquale.search.mcts.common.AbstractIntentionalSelectionTraversalPolicy;
import com.dipasquale.search.mcts.concurrent.ConcurrentSearchNode;
import com.dipasquale.synchronization.InterruptedRuntimeException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;

public final class ConcurrentIntentionalSelectionTraversalPolicy<TAction extends Action, TEdge extends Edge, TState extends State<TAction, TState>> extends AbstractIntentionalSelectionTraversalPolicy<TAction, TEdge, TState, ConcurrentSearchNode<TAction, TEdge, TState>, ConcurrentIntentionalSelectionTraversalPolicy.Context<TAction, TEdge, TState>> {
    public ConcurrentIntentionalSelectionTraversalPolicy(final SelectionConfidenceCalculator<TEdge> selectionConfidenceCalculator) {
        super(selectionConfidenceCalculator);
    }

    @Override
    protected ConcurrentIntentionalSelectionTraversalPolicy.Context<TAction, TEdge, TState> createContext() {
        return new Context<>();
    }

    private void collectConfidenceCalculation(final Lock lock, final ConcurrentIntentionalSelectionTraversalPolicy.Context<TAction, TEdge, TState> context, final ConcurrentSearchNode<TAction, TEdge, TState> childSearchNode, final TEdge parentEdge, final OptimalPairSelector<Float, Integer> optimalChildIndexSelector, final int index) {
        context.childSearchNodeIndexes.put(childSearchNode, index);

        if (lock.tryLock()) {
            try {
                super.collectConfidenceCalculation(context, childSearchNode, parentEdge, optimalChildIndexSelector, index);
            } finally {
                lock.unlock();
            }
        }
    }

    @Override
    protected void collectConfidenceCalculation(final ConcurrentIntentionalSelectionTraversalPolicy.Context<TAction, TEdge, TState> context, final ConcurrentSearchNode<TAction, TEdge, TState> childSearchNode, final TEdge parentEdge, final OptimalPairSelector<Float, Integer> optimalChildIndexSelector, final int index) {
        collectConfidenceCalculation(childSearchNode.getBackPropagatingLock().readLock(), context, childSearchNode, parentEdge, optimalChildIndexSelector, index);
    }

    @Override
    protected int getOptimalIndex(final ConcurrentIntentionalSelectionTraversalPolicy.Context<TAction, TEdge, TState> context, final ConcurrentSearchNode<TAction, TEdge, TState> parentSearchNode, final OptimalPairSelector<Float, Integer> optimalChildIndexSelector) {
        while (optimalChildIndexSelector.getReplacedCount() == 0) {
            TEdge parentEdge = parentSearchNode.getEdge();

            try {
                ReadWriteLock readWriteLock = parentSearchNode.getBackPropagatingLockRing().awaitAnyUnlocked();
                Lock lock = readWriteLock.readLock();

                if (lock.tryLock()) {
                    try {
                        ConcurrentSearchNode<TAction, TEdge, TState> childSearchNode = parentSearchNode.getBackPropagatingLockRing().identify(readWriteLock);
                        int index = context.childSearchNodeIndexes.get(childSearchNode);

                        super.collectConfidenceCalculation(context, childSearchNode, parentEdge, optimalChildIndexSelector, index);

                        return super.getOptimalIndex(context, parentSearchNode, optimalChildIndexSelector);
                    } finally {
                        lock.unlock();
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();

                throw new InterruptedRuntimeException("unable to get the optimal index", e);
            }
        }

        return super.getOptimalIndex(context, parentSearchNode, optimalChildIndexSelector);
    }

    private ConcurrentSearchNode<TAction, TEdge, TState> next(final Lock lock, final int simulations, final ConcurrentSearchNode<TAction, TEdge, TState> searchNode) {
        lock.lock();

        try {
            return super.next(simulations, searchNode);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public ConcurrentSearchNode<TAction, TEdge, TState> next(final int simulations, final ConcurrentSearchNode<TAction, TEdge, TState> searchNode) {
        return next(searchNode.getExpandingLock().readLock(), simulations, searchNode);
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class Context<TAction extends Action, TEdge extends Edge, TState extends State<TAction, TState>> {
        private final Map<ConcurrentSearchNode<TAction, TEdge, TState>, Integer> childSearchNodeIndexes = new IdentityHashMap<>();
    }
}
