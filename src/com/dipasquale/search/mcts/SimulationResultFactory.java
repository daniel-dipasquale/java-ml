package com.dipasquale.search.mcts;

@FunctionalInterface
interface SimulationResultFactory<T extends State> {
    SimulationResult<T> create(Node<T> rootNode, Node<T> leafNode, int statusId);
}
