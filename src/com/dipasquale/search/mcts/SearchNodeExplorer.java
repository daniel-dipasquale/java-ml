package com.dipasquale.search.mcts;

public interface SearchNodeExplorer<TAction, TEdge extends Edge, TState extends State<TAction, TState>, TSearchNode extends SearchNode<TAction, TEdge, TState, TSearchNode>> {
    boolean isFullyExplored(TSearchNode searchNode);

    boolean notifyParentIsFullyExplored(TSearchNode searchNode);
}
