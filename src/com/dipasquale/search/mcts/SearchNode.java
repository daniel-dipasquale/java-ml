package com.dipasquale.search.mcts;

public interface SearchNode<TAction, TEdge extends Edge, TState extends State<TAction, TState>, TSearchNode extends SearchNode<TAction, TEdge, TState, TSearchNode>> {
    int NO_SELECTED_EXPLORABLE_CHILD_KEY = -1;

    TSearchNode getParent();

    SearchResult<TAction, TState> getResult();

    void reinitialize(SearchResult<TAction, TState> result);

    default int getActionId() {
        return getResult().getActionId();
    }

    default TAction getAction() {
        return getResult().getAction();
    }

    TEdge getEdge();

    default StateId getStateId() {
        return getResult().getStateId();
    }

    default TState getState() {
        return getResult().getState();
    }

    Iterable<TSearchNode> createAllPossibleChildren(EdgeFactory<TEdge> edgeFactory);

    SearchNodeGroup<TAction, TEdge, TState, TSearchNode> getUnexploredChildren();

    void setUnexploredChildren(SearchNodeGroup<TAction, TEdge, TState, TSearchNode> unexploredChildren);

    SearchNodeGroup<TAction, TEdge, TState, TSearchNode> getExplorableChildren();

    void setExplorableChildren(SearchNodeGroup<TAction, TEdge, TState, TSearchNode> explorableChildren);

    SearchNodeGroup<TAction, TEdge, TState, TSearchNode> getFullyExploredChildren();

    void setFullyExploredChildren(SearchNodeGroup<TAction, TEdge, TState, TSearchNode> fullyExploredChildren);

    int getSelectedExplorableChildKey();

    void setSelectedExplorableChildKey(int key);

    boolean isExpanded();

    boolean isFullyExplored();

    @Override
    String toString();
}
