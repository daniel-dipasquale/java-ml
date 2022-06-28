package com.dipasquale.search.mcts;

public interface SearchNodeGroupProvider<TAction, TEdge extends Edge, TState extends State<TAction, TState>, TSearchNode extends SearchNode<TAction, TEdge, TState, TSearchNode>> {
    SearchNodeGroup<TAction, TEdge, TState, TSearchNode> getEmpty();

    SearchNodeGroup<TAction, TEdge, TState, TSearchNode> create(Iterable<TSearchNode> searchNodes);
}
