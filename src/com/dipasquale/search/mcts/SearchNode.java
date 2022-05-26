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
