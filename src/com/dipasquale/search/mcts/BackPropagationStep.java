package com.dipasquale.search.mcts;

public interface BackPropagationStep<TAction extends Action, TEdge extends Edge, TState extends State<TAction, TState>, TSearchNode extends SearchNode<TAction, TEdge, TState, TSearchNode>, TContext> {
    TContext createContext(TSearchNode leafSearchNode);

    void process(TContext context, TSearchNode currentSearchNode);
}
