package com.dipasquale.search.mcts.buffer;

import com.dipasquale.search.mcts.Edge;
import com.dipasquale.search.mcts.SearchNode;
import com.dipasquale.search.mcts.State;
import com.dipasquale.search.mcts.StateId;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public final class Generation<TAction, TEdge extends Edge, TState extends State<TAction, TState>, TSearchNode extends SearchNode<TAction, TEdge, TState, TSearchNode>> {
    @Getter(AccessLevel.PACKAGE)
    @Setter(AccessLevel.PRIVATE)
    private TSearchNode searchNode;
    private StateId parentStateId; // NOTE: avoids garbage collection
    private final Map<StateId, Generation<TAction, TEdge, TState, TSearchNode>> descendents = new HashMap<>();

    boolean isInitialized() {
        return searchNode != null;
    }

    Generation<TAction, TEdge, TState, TSearchNode> getDescendant(final StateId stateId) {
        return descendents.get(stateId);
    }

    void replace(final TSearchNode searchNode) {
        setSearchNode(searchNode);
        parentStateId = searchNode.getStateId().getParent();
        descendents.clear();
    }

    private void allocateDescendant(final TSearchNode childSearchNode) {
        StateId stateId = childSearchNode.getStateId();
        Generation<TAction, TEdge, TState, TSearchNode> descendantGeneration = new Generation<>();

        descendents.put(stateId, descendantGeneration);
        descendantGeneration.replace(childSearchNode);
    }

    void allocateDescendants(final TSearchNode searchNode) {
        for (TSearchNode childSearchNode : searchNode.getUnexploredChildren()) {
            allocateDescendant(childSearchNode);
        }

        for (TSearchNode childSearchNode : searchNode.getExplorableChildren()) {
            allocateDescendant(childSearchNode);
        }

        for (TSearchNode childSearchNode : searchNode.getFullyExploredChildren()) {
            allocateDescendant(childSearchNode);
        }
    }

    public void clear() {
        searchNode = null;
        parentStateId = null;
        descendents.clear();
    }
}
