package com.dipasquale.search.mcts;

public interface SearchNodeManager<TAction extends Action, TEdge extends Edge, TState extends State<TAction, TState>, TSearchNode extends SearchNode<TAction, TEdge, TState, TSearchNode>> {
    boolean isFullyExplored(TSearchNode searchNode);

    boolean declareFullyExplored(TSearchNode searchNode);
}
