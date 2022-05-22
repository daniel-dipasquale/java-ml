package com.dipasquale.search.mcts;

@FunctionalInterface
public interface SearchStrategy<TAction extends Action, TEdge extends Edge, TState extends State<TAction, TState>, TSearchNode extends SearchNode<TAction, TEdge, TState, TSearchNode>> {
    void process(TSearchNode rootSearchNode);
}
