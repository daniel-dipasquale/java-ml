package com.dipasquale.search.mcts;

import com.dipasquale.data.structure.collection.ListSupport;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class StandardSearchNodeGroupProvider<TAction, TEdge extends Edge, TState extends State<TAction, TState>> extends AbstractSearchNodeGroupProvider<TAction, TEdge, TState, StandardSearchNode<TAction, TEdge, TState>> {
    private static final StandardSearchNodeGroupProvider<?, ?, ?> INSTANCE = new StandardSearchNodeGroupProvider<>();

    public static <TAction, TEdge extends Edge, TState extends State<TAction, TState>> StandardSearchNodeGroupProvider<TAction, TEdge, TState> getInstance() {
        return (StandardSearchNodeGroupProvider<TAction, TEdge, TState>) INSTANCE;
    }

    private static <T> List<T> ensureList(final Iterable<T> iterable) {
        if (iterable instanceof List<?>) {
            return (List<T>) iterable;
        }

        if (iterable == null) {
            return new ArrayList<>();
        }

        return ListSupport.copyOf(iterable);
    }

    @Override
    public SearchNodeGroup<TAction, TEdge, TState, StandardSearchNode<TAction, TEdge, TState>> create(final Iterable<StandardSearchNode<TAction, TEdge, TState>> searchNodes) {
        List<StandardSearchNode<TAction, TEdge, TState>> fixedSearchNodes = ensureList(searchNodes);

        return new IndexedSearchNodeGroup<>(fixedSearchNodes);
    }
}
