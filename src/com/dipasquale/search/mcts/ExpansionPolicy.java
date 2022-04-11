package com.dipasquale.search.mcts;

@FunctionalInterface
public interface ExpansionPolicy<TAction extends Action, TEdge extends Edge, TState extends State<TAction, TState>, TSearchNode extends SearchNode<TAction, TEdge, TState, TSearchNode>> {
    void expand(TSearchNode searchNode);
}
