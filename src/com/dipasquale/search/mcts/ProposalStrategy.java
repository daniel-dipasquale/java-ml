package com.dipasquale.search.mcts;

@FunctionalInterface
public interface ProposalStrategy<TAction extends Action, TEdge extends Edge, TState extends State<TAction, TState>, TSearchNode extends SearchNode<TAction, TEdge, TState, TSearchNode>> {
    TSearchNode proposeBestNode(int simulations, int depth, Iterable<TSearchNode> searchNodes);
}
