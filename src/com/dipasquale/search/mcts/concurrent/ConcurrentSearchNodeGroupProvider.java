package com.dipasquale.search.mcts.concurrent;

import com.dipasquale.search.mcts.AbstractSearchNodeGroupProvider;
import com.dipasquale.search.mcts.HashedSearchNodeGroup;
import com.dipasquale.search.mcts.SearchNodeGroup;
import com.dipasquale.search.mcts.State;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class ConcurrentSearchNodeGroupProvider<TAction, TEdge extends ConcurrentEdge, TState extends State<TAction, TState>> extends AbstractSearchNodeGroupProvider<TAction, TEdge, TState, ConcurrentSearchNode<TAction, TEdge, TState>> {
    private static final ConcurrentSearchNodeGroupProvider<?, ?, ?> INSTANCE = new ConcurrentSearchNodeGroupProvider<>();

    public static <TAction, TEdge extends ConcurrentEdge, TState extends State<TAction, TState>> ConcurrentSearchNodeGroupProvider<TAction, TEdge, TState> getInstance() {
        return (ConcurrentSearchNodeGroupProvider<TAction, TEdge, TState>) INSTANCE;
    }

    @Override
    public SearchNodeGroup<TAction, TEdge, TState, ConcurrentSearchNode<TAction, TEdge, TState>> create(final Iterable<ConcurrentSearchNode<TAction, TEdge, TState>> searchNodes) {
        if (searchNodes == null) {
            return new HashedSearchNodeGroup<>();
        }

        return new HashedSearchNodeGroup<>(searchNodes);
    }
}
