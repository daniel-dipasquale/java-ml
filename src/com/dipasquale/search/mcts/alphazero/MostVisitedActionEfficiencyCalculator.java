package com.dipasquale.search.mcts.alphazero;

import com.dipasquale.search.mcts.Action;
import com.dipasquale.search.mcts.ActionEfficiencyCalculator;
import lombok.Builder;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Builder
public final class MostVisitedActionEfficiencyCalculator<T extends Action> implements ActionEfficiencyCalculator<T, AlphaZeroEdge> {
    private final TemperatureController temperatureController;

    @Override
    public float calculate(final int depth, final T action, final AlphaZeroEdge edge) {
        if (temperatureController == null) {
            return (float) edge.getVisited();
        }

        return temperatureController.getTemperature(depth, (float) edge.getVisited());
    }
}
