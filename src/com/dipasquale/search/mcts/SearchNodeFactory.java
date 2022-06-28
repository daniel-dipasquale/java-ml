package com.dipasquale.search.mcts;

public interface SearchNodeFactory<TAction, TEdge extends Edge, TState extends State<TAction, TState>, TSearchNode extends SearchNode<TAction, TEdge, TState, TSearchNode>> {
    EdgeFactory<TEdge> getEdgeFactory();

    TSearchNode createRoot(SearchResult<TAction, TState> result);
}
