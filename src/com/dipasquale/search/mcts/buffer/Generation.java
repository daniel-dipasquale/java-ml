package com.dipasquale.search.mcts.buffer;

import com.dipasquale.search.mcts.Action;
import com.dipasquale.search.mcts.Edge;
import com.dipasquale.search.mcts.SearchNode;
import com.dipasquale.search.mcts.State;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.Map;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class Generation<TAction extends Action, TEdge extends Edge, TState extends State<TAction, TState>, TSearchNode extends SearchNode<TAction, TEdge, TState, TSearchNode>, TGeneration extends Generation<TAction, TEdge, TState, TSearchNode, TGeneration>> {
    @Getter(AccessLevel.PACKAGE)
    @Setter(AccessLevel.PRIVATE)
    private TSearchNode searchNode;
    private TreeId parentTreeId; // NOTE: avoids garbage collection
    private final Map<TreeId, TGeneration> descendents;

    public boolean isInitialized() {
        return searchNode != null;
    }

    public TreeId getTreeId() {
        return searchNode.getAction().getTreeId();
    }

    public void replace(final TSearchNode searchNode) {
        setSearchNode(searchNode);
        parentTreeId = searchNode.getAction().getTreeId().getParent();
    }

    protected abstract TGeneration createNext();

    public TGeneration getOrCreateNext(final TreeId treeId) {
        return descendents.computeIfAbsent(treeId, __ -> createNext());
    }

    public TGeneration getNext(final TreeId treeId) {
        return descendents.get(treeId);
    }

    public void clear() {
        searchNode = null;
        parentTreeId = null;
        descendents.clear();
    }
}
