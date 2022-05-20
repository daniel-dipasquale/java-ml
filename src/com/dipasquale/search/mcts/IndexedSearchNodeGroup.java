package com.dipasquale.search.mcts;

import com.dipasquale.common.Record;
import lombok.RequiredArgsConstructor;

import java.util.Iterator;
import java.util.List;

@RequiredArgsConstructor
public final class IndexedSearchNodeGroup<TAction extends Action, TEdge extends Edge, TState extends State<TAction, TState>, TSearchNode extends SearchNode<TAction, TEdge, TState, TSearchNode>> implements SearchNodeGroup<TAction, TEdge, TState, TSearchNode> {
    private final List<TSearchNode> searchNodes;

    @Override
    public int size() {
        return searchNodes.size();
    }

    @Override
    public boolean isEmpty() {
        return searchNodes.isEmpty();
    }

    @Override
    public TSearchNode getByIndex(final int index) {
        return searchNodes.get(index);
    }

    @Override
    public Record<Integer, TSearchNode> getRecordByIndex(final int index) {
        return new Record<>(index, getByIndex(index));
    }

    @Override
    public void swap(final int fromIndex, final int toIndex) {
        TSearchNode replacedSearchNode = searchNodes.set(toIndex, searchNodes.get(fromIndex));

        searchNodes.set(fromIndex, replacedSearchNode);
    }

    @Override
    public int add(final TSearchNode searchNode) {
        int size = searchNodes.size();

        searchNodes.add(searchNode);

        return size;
    }

    @Override
    public TSearchNode removeByIndex(final int index) {
        return searchNodes.remove(index);
    }

    @Override
    public Record<Integer, TSearchNode> removeRecordByIndex(final int index) {
        TSearchNode searchNode = removeByIndex(index);

        return new Record<>(index, searchNode);
    }

    @Override
    public TSearchNode removeByKey(final int key) {
        return searchNodes.remove(key);
    }

    @Override
    public Iterator<TSearchNode> iterator() {
        return searchNodes.iterator();
    }
}
