package com.dipasquale.search.mcts;

public interface Buffer<TAction extends Action, TEdge extends Edge, TState extends State<TAction, TState>, TSearchNode extends SearchNode<TAction, TEdge, TState, TSearchNode>> {
    TSearchNode recallOrCreate(TState state);

    void store(TSearchNode searchNode);

    void clear();
}
