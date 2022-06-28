package com.dipasquale.search.mcts;

import com.dipasquale.common.Record;
import com.dipasquale.data.structure.group.ListSetGroup;

import java.util.Iterator;

public final class HashedSearchNodeGroup<TAction, TEdge extends Edge, TState extends State<TAction, TState>, TSearchNode extends SearchNode<TAction, TEdge, TState, TSearchNode>> implements SearchNodeGroup<TAction, TEdge, TState, TSearchNode> {
    private final ListSetGroup<Integer, TSearchNode> searchNodes;

    private static <TAction, TEdge extends Edge, TState extends State<TAction, TState>, TSearchNode extends SearchNode<TAction, TEdge, TState, TSearchNode>> int getKey(final TSearchNode searchNode) {
        return searchNode.getActionId();
    }

    private static <TAction, TEdge extends Edge, TState extends State<TAction, TState>, TSearchNode extends SearchNode<TAction, TEdge, TState, TSearchNode>> ListSetGroup<Integer, TSearchNode> createSearchNodes() {
        return new ListSetGroup<>(HashedSearchNodeGroup::getKey);
    }

    public HashedSearchNodeGroup() {
        this.searchNodes = createSearchNodes();
    }

    private static <TAction, TEdge extends Edge, TState extends State<TAction, TState>, TSearchNode extends SearchNode<TAction, TEdge, TState, TSearchNode>> ListSetGroup<Integer, TSearchNode> createInitializedSearchNodes(final Iterable<TSearchNode> searchNodes) {
        ListSetGroup<Integer, TSearchNode> fixedSearchNodes = createSearchNodes();

        for (TSearchNode searchNode : searchNodes) {
            fixedSearchNodes.put(searchNode);
        }

        return fixedSearchNodes;
    }

    public HashedSearchNodeGroup(final Iterable<TSearchNode> searchNodes) {
        this.searchNodes = createInitializedSearchNodes(searchNodes);
    }

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
        return searchNodes.getByIndex(index);
    }

    @Override
    public Record<Integer, TSearchNode> getRecordByIndex(final int index) {
        TSearchNode searchNode = getByIndex(index);

        return new Record<>(getKey(searchNode), searchNode);
    }

    @Override
    public void swap(final int fromIndex, final int toIndex) {
        searchNodes.swap(fromIndex, toIndex);
    }

    @Override
    public int add(final TSearchNode searchNode) {
        int key = getKey(searchNode);

        searchNodes.put(searchNode);

        return key;
    }

    @Override
    public TSearchNode removeByIndex(final int index) {
        return searchNodes.removeByIndex(index);
    }

    @Override
    public Record<Integer, TSearchNode> removeRecordByIndex(final int index) {
        TSearchNode searchNode = removeByIndex(index);

        return new Record<>(getKey(searchNode), searchNode);
    }

    @Override
    public TSearchNode removeByKey(final int key) {
        return searchNodes.removeByKey(key);
    }

    @Override
    public Iterator<TSearchNode> iterator() {
        return searchNodes.sortedIterator();
    }
}
