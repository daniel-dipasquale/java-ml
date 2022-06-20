package com.dipasquale.search.mcts;

@FunctionalInterface
public interface SearchNodeFactory<TAction extends Action, TEdge extends Edge, TState extends State<TAction, TState>, TSearchNode extends SearchNode<TAction, TEdge, TState, TSearchNode>> {
    TSearchNode createRoot(SearchNodeResult<TAction, TState> result, EdgeFactory<TEdge> edgeFactory);
}
