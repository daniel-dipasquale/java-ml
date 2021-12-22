package com.dipasquale.search.mcts;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class StatelessSimulationResultFactory<T extends State> implements SimulationResultFactory<T> {
    @Override
    public SimulationResult<T> create(final Node<T> rootNode, final Node<T> leafNode, final int statusId) {
        return new SimulationResult<>(rootNode, statusId);
    }
}
