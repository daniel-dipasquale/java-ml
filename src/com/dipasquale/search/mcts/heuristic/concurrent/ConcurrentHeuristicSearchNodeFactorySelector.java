package com.dipasquale.search.mcts.heuristic.concurrent;

import com.dipasquale.search.mcts.Action;
import com.dipasquale.search.mcts.SearchNodeFactory;
import com.dipasquale.search.mcts.State;
import com.dipasquale.search.mcts.concurrent.ConcurrentSearchNode;
import com.dipasquale.search.mcts.concurrent.ConcurrentSearchNodeFactorySelector;
import com.dipasquale.search.mcts.concurrent.EdgeTraversalLockType;
import com.dipasquale.search.mcts.concurrent.SharedLockSearchNodeFactory;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class ConcurrentHeuristicSearchNodeFactorySelector<TAction extends Action, TState extends State<TAction, TState>> implements ConcurrentSearchNodeFactorySelector<TAction, ConcurrentHeuristicEdge, TState> {
    private static final ConcurrentHeuristicSearchNodeFactorySelector<?, ?> INSTANCE = new ConcurrentHeuristicSearchNodeFactorySelector<>();

    public static <TAction extends Action, TState extends State<TAction, TState>> ConcurrentHeuristicSearchNodeFactorySelector<TAction, TState> getInstance() {
        return (ConcurrentHeuristicSearchNodeFactorySelector<TAction, TState>) INSTANCE;
    }

    @Override
    public SearchNodeFactory<TAction, ConcurrentHeuristicEdge, TState, ConcurrentSearchNode<TAction, ConcurrentHeuristicEdge, TState>> select(final EdgeTraversalLockType edgeTraversalLockType, final List<Long> threadIds) {
        return switch (edgeTraversalLockType) {
            case SHARED -> new SharedLockSearchNodeFactory<>(SharedLockHeuristicEdgeFactory.getInstance(), threadIds);

            case RCU -> new RcuLockHeuristicSearchNodeFactory<>(threadIds);
        };
    }
}
