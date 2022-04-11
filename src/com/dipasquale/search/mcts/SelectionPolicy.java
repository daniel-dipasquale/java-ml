package com.dipasquale.search.mcts;

@FunctionalInterface
public interface SelectionPolicy<TAction extends Action, TEdge extends Edge, TState extends State<TAction, TState>, TSearchNode extends SearchNode<TAction, TEdge, TState, TSearchNode>> {
    TSearchNode select(int simulations, TSearchNode rootSearchNode);
}
