package com.dipasquale.search.mcts;

@FunctionalInterface
interface SimulationResultFactory<T extends State> {
    SimulationResult<T> create(SearchNode<T> rootSearchNode, SearchNode<T> leafSearchNode, int statusId);
}
