package com.dipasquale.simulation.game2048;

import com.dipasquale.search.mcts.MonteCarloTreeSearch;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class Game {
    private static final boolean IS_SIMULATION = false;
    private final ValuedTileSupport valuedTileSupport;
    private final int maximumValue;

    private GameState createState(final Player player) {
        int victoryValue = (int) (Math.log(maximumValue) / Math.log(2D));
        GameState state = new GameState(valuedTileSupport, victoryValue);

        player.initializeState(state);

        return state;
    }

    public GameResult play(final Player player) {
        GameState state = createState(player);
        int statusId = state.getStatusId();

        while (statusId == MonteCarloTreeSearch.IN_PROGRESS_STATUS_ID) {
            GameAction action = player.createNextAction(state);

            state = state.accept(action, IS_SIMULATION);
            statusId = state.getStatusId();
        }

        GameResult result = new GameResult(state);

        player.accept(result);

        return result;
    }
}
