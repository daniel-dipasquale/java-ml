package com.dipasquale.search.mcts;

public interface BackPropagationStep<TAction extends Action, TEdge extends Edge, TState extends State<TAction, TState>, TContext> {
    TContext createContext(SearchNode<TAction, TEdge, TState> leafSearchNode);

    void process(TContext context, SearchNode<TAction, TEdge, TState> currentSearchNode);
}
