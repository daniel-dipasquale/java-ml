package com.dipasquale.search.mcts;

@FunctionalInterface
interface BackPropagationPolicy<T extends State> {
    void process(SearchNode<T> rootSearchNode, SearchNode<T> leafSearchNode, int statusId);
}
