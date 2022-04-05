package com.dipasquale.simulation.game2048.heuristic;

import com.dipasquale.search.mcts.common.RewardHeuristic;
import com.dipasquale.simulation.game2048.Game;
import com.dipasquale.simulation.game2048.GameAction;
import com.dipasquale.simulation.game2048.GameState;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class FreeTileRewardHeuristic implements RewardHeuristic<GameAction, GameState> {
    private static final FreeTileRewardHeuristic INSTANCE = new FreeTileRewardHeuristic();

    public static FreeTileRewardHeuristic getInstance() {
        return INSTANCE;
    }

    @Override
    public float estimate(final GameState state) {
        int freeTileCount = state.isIntentional() ? 15 : 14;
        int valuedTileCount = state.getValuedTileCount() - Game.BOARD_SQUARE_LENGTH + freeTileCount;
        float probability = (float) (freeTileCount - valuedTileCount) / (float) freeTileCount;

        assert Float.compare(probability, 0f) >= 0 && Float.compare(probability, 1f) <= 0;

        return RewardHeuristic.convertProbability(probability);
    }
}
