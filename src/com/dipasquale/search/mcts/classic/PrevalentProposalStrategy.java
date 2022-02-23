package com.dipasquale.search.mcts.classic;

import com.dipasquale.search.mcts.alphazero.ProposalStrategy;
import lombok.Builder;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Builder
public final class PrevalentProposalStrategy implements ProposalStrategy<ClassicEdge> {
    private final float winningFactor;
    private final float notLosingFactor;

    @Override
    public float calculateEfficiency(final int simulations, final ClassicEdge edge) {
        float visited = (float) edge.getVisited();
        float won = (float) edge.getWon();
        float drawn = (float) edge.getDrawn();
        float wonRate = won / visited;
        float notLostRate = (won + drawn) / visited;

        return winningFactor * wonRate + notLosingFactor * notLostRate;
    }
}
