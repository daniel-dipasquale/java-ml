package com.dipasquale.simulation.game2048;

import com.dipasquale.search.mcts.common.ValueHeuristic;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class FreeTileValueHeuristic implements ValueHeuristic<GameAction, GameState> {
    private static final FreeTileValueHeuristic INSTANCE = new FreeTileValueHeuristic();

    public static FreeTileValueHeuristic getInstance() {
        return INSTANCE;
    }

    @Override
    public float estimate(final GameState state) {
        int freeTileCount = state.isIntentional() ? 15 : 14;
        int valuedTileCount = state.getValuedTileCount() - Game.BOARD_SQUARE_LENGTH + freeTileCount;
        float probability = (float) (freeTileCount - valuedTileCount) / (float) freeTileCount;

        assert Float.compare(probability, 0f) >= 0 && Float.compare(probability, 1f) <= 0;

        return ValueHeuristic.convertProbability(probability);
    }
}
