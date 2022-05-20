package com.dipasquale.search.mcts;

public interface SearchNode<TAction extends Action, TEdge extends Edge, TState extends State<TAction, TState>, TSearchNode extends SearchNode<TAction, TEdge, TState, TSearchNode>> {
    int NO_SELECTED_EXPLORABLE_CHILD_KEY = -1;

    TSearchNode getParent();

    TAction getAction();

    TEdge getEdge();

    TState getState();

    void reinitialize(TState state);

    Iterable<TSearchNode> createAllPossibleChildNodes(EdgeFactory<TEdge> edgeFactory);

    SearchNodeGroup<TAction, TEdge, TState, TSearchNode> getUnexploredChildren();

    SearchNodeGroup<TAction, TEdge, TState, TSearchNode> getExplorableChildren();

    SearchNodeGroup<TAction, TEdge, TState, TSearchNode> getFullyExploredChildren();

    void setUnexploredChildren(SearchNodeGroup<TAction, TEdge, TState, TSearchNode> unexploredChildren);

    void setExplorableChildren(SearchNodeGroup<TAction, TEdge, TState, TSearchNode> explorableChildren);

    void setFullyExploredChildren(SearchNodeGroup<TAction, TEdge, TState, TSearchNode> fullyExploredChildren);

    int getSelectedExplorableChildKey();

    void setSelectedExplorableChildKey(int key);

    boolean isExpanded();

    boolean isFullyExplored();

    @Override
    String toString();
}
