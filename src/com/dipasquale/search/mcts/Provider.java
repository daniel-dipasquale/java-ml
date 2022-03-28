package com.dipasquale.search.mcts;

public interface Provider<TAction extends Action, TEdge extends Edge, TState extends State<TAction, TState>> {
    boolean isAllowedToCollect();

    SearchNode<TAction, TEdge, TState> recallOrCreate(TState state);

    void collect(SearchNode<TAction, TEdge, TState> searchNode);

    void clear();
}
