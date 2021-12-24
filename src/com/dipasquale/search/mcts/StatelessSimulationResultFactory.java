package com.dipasquale.search.mcts;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class StatelessSimulationResultFactory<T extends State> implements SimulationResultFactory<T> {
    @Override
    public SimulationResult<T> create(final SearchNode<T> rootSearchNode, final SearchNode<T> leafSearchNode, final int statusId) {
        return new SimulationResult<>(rootSearchNode, statusId);
    }
}
