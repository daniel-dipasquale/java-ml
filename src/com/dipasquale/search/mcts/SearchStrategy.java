package com.dipasquale.search.mcts;

public interface SearchStrategy<TAction extends Action, TEdge extends Edge, TState extends State<TAction, TState>, TSearchNode extends SearchNode<TAction, TEdge, TState, TSearchNode>> {
    void expand(TSearchNode rootSearchNode);
}
