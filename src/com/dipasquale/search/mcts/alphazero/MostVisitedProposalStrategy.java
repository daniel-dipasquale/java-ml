package com.dipasquale.search.mcts.alphazero;

import lombok.Builder;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Builder
public final class MostVisitedProposalStrategy implements ProposalStrategy<AlphaZeroEdge> {
    private final TemperatureCalculator temperatureCalculator;

    @Override
    public float calculateEfficiency(final int simulations, final AlphaZeroEdge edge) {
        if (temperatureCalculator == null) {
            return (float) edge.getVisited();
        }

        return temperatureCalculator.calculate(simulations, (float) edge.getVisited());
    }
}
