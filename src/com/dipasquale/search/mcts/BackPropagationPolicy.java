package com.dipasquale.search.mcts;

interface BackPropagationPolicy<T extends State> {
    boolean process(SearchNode<T> searchNode, int statusId);
}
