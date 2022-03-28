package com.dipasquale.search.mcts.classic;

import com.dipasquale.search.mcts.ActionEfficiencyCalculator;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
final class PrevalentActionEfficiencyCalculator implements ActionEfficiencyCalculator<ClassicEdge> {
    private static final float WIN_FACTOR = 2f;
    private static final float DRAW_FACTOR = 1f;
    private static final float LOSE_FACTOR = -2f;
    private static final PrevalentActionEfficiencyCalculator INSTANCE = new PrevalentActionEfficiencyCalculator();

    public static PrevalentActionEfficiencyCalculator getInstance() {
        return INSTANCE;
    }

    @Override
    public float calculate(final int depth, final ClassicEdge edge) {
        int visited = edge.getVisited();

        if (visited == 0) {
            return 0f;
        }

        float fixedVisited = (float) visited;
        float won = (float) edge.getWon();
        float drawn = (float) edge.getDrawn();
        float lost = (float) visited - edge.getWon() - edge.getDrawn();
        float wonRate = won / fixedVisited;
        float drawRate = drawn / fixedVisited;
        float lostRate = lost / fixedVisited;

        return WIN_FACTOR * wonRate + DRAW_FACTOR * drawRate + lostRate * LOSE_FACTOR;
    }
}
