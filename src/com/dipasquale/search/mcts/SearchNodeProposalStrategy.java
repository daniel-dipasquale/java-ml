package com.dipasquale.search.mcts;

@FunctionalInterface
public interface SearchNodeProposalStrategy<TAction extends Action, TEdge extends Edge, TState extends State<TAction, TState>> {
    SearchNode<TAction, TEdge, TState> proposeBestNode(int simulations, Iterable<SearchNode<TAction, TEdge, TState>> nodes);
}
