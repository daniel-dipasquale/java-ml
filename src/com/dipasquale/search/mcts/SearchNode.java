package com.dipasquale.search.mcts;

import java.util.List;

public interface SearchNode<TAction extends Action, TEdge extends Edge, TState extends State<TAction, TState>, TSearchNode extends SearchNode<TAction, TEdge, TState, TSearchNode>> {
    TSearchNode getParent();

    void reinitialize(TState state);

    List<TSearchNode> createAllPossibleChildNodes(EdgeFactory<TEdge> edgeFactory);

    void initializeState();

    boolean isExpanded();

    boolean isFullyExplored();

    TAction getAction();

    TEdge getEdge();

    TState getState();

    List<TSearchNode> getUnexploredChildren();

    List<TSearchNode> getExplorableChildren();

    List<TSearchNode> getFullyExploredChildren();

    int getSelectedExplorableChildIndex();

    void setUnexploredChildren(List<TSearchNode> unexploredChildren);

    void setExplorableChildren(List<TSearchNode> explorableChildren);

    void setFullyExploredChildren(List<TSearchNode> fullyExploredChildren);

    void setSelectedExplorableChildIndex(int selectedExplorableChildIndex);

    @Override
    String toString();
}
