package com.dipasquale.search.mcts;

interface BackPropagationPolicy<T extends State> {
    boolean process(Node<T> node, int statusId);
}
