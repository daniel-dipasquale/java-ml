package com.dipasquale.search.mcts.classic.concurrent;

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
public final class ConcurrentClassicSearchNodeFactorySelector<TAction extends Action, TState extends State<TAction, TState>> implements ConcurrentSearchNodeFactorySelector<TAction, ConcurrentClassicEdge, TState> {
    private static final ConcurrentClassicSearchNodeFactorySelector<?, ?> INSTANCE = new ConcurrentClassicSearchNodeFactorySelector<>();

    public static <TAction extends Action, TState extends State<TAction, TState>> ConcurrentClassicSearchNodeFactorySelector<TAction, TState> getInstance() {
        return (ConcurrentClassicSearchNodeFactorySelector<TAction, TState>) INSTANCE;
    }

    @Override
    public SearchNodeFactory<TAction, ConcurrentClassicEdge, TState, ConcurrentSearchNode<TAction, ConcurrentClassicEdge, TState>> select(final EdgeTraversalLockType edgeTraversalLockType, final List<Long> threadIds) {
        return switch (edgeTraversalLockType) {
            case SHARED -> new SharedLockSearchNodeFactory<>(SharedLockClassicEdgeFactory.getInstance(), threadIds);

            case RCU -> new RcuLockClassicSearchNodeFactory<>(threadIds);
        };
    }
}
