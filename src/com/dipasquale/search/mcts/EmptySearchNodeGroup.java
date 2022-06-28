package com.dipasquale.search.mcts;

import com.dipasquale.common.Record;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.Iterator;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class EmptySearchNodeGroup<TAction, TEdge extends Edge, TState extends State<TAction, TState>, TSearchNode extends SearchNode<TAction, TEdge, TState, TSearchNode>> implements SearchNodeGroup<TAction, TEdge, TState, TSearchNode> {
    private static final EmptyIterator<?> EMPTY_ITERATOR = new EmptyIterator<>();
    private static final EmptySearchNodeGroup<?, ?, ?, ?> INSTANCE = new EmptySearchNodeGroup<>();

    public static <TAction, TEdge extends Edge, TState extends State<TAction, TState>, TSearchNode extends SearchNode<TAction, TEdge, TState, TSearchNode>> EmptySearchNodeGroup<TAction, TEdge, TState, TSearchNode> getInstance() {
        return (EmptySearchNodeGroup<TAction, TEdge, TState, TSearchNode>) INSTANCE;
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public boolean isEmpty() {
        return true;
    }

    @Override
    public TSearchNode getByIndex(final int index) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Record<Integer, TSearchNode> getRecordByIndex(final int index) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void swap(final int fromIndex, final int toIndex) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int add(final TSearchNode searchNode) {
        throw new UnsupportedOperationException();
    }

    @Override
    public TSearchNode removeByIndex(final int index) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Record<Integer, TSearchNode> removeRecordByIndex(final int index) {
        throw new UnsupportedOperationException();
    }

    @Override
    public TSearchNode removeByKey(final int key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Iterator<TSearchNode> iterator() {
        return (EmptyIterator<TSearchNode>) EMPTY_ITERATOR;
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class EmptyIterator<T> implements Iterator<T> {
        @Override
        public boolean hasNext() {
            return false;
        }

        @Override
        public T next() {
            throw new UnsupportedOperationException();
        }
    }
}
