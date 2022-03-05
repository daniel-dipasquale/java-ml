package com.dipasquale.search.mcts;

public interface SearchNodeProvider<TAction extends Action, TEdge extends Edge, TState extends State<TAction, TState>> {
    SearchNode<TAction, TEdge, TState> provide(TState state);

    boolean registerIfApplicable(SearchNode<TAction, TEdge, TState> node);

    static <TAction extends Action, TEdge extends Edge, TState extends State<TAction, TState>> SearchNode<TAction, TEdge, TState> createRootNode(final EdgeFactory<TEdge> edgeFactory, final TState state, final int depth) {
        return new SearchNode<>(edgeFactory.create(null), state, depth);
    }
}
