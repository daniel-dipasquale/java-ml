package com.dipasquale.simulation.game2048;

import com.dipasquale.search.mcts.MonteCarloTreeSearch;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class Game {
    private static final boolean DEBUG = false;
    private final ValuedTileSupport valuedTileSupport;
    private final int victoryValue;
    private final Player valuedTileAdder;

    public GameResult play(final Player player) {
        Player[] players = new Player[]{valuedTileAdder, player};
        GameState state = new GameState(valuedTileSupport, victoryValue);
        int statusId = state.getStatusId();

        for (int index = 0; statusId == MonteCarloTreeSearch.IN_PROGRESS_STATUS_ID; index = state.getNextParticipantId() - 1) {
            GameAction action = players[index].createNextAction(state);

            state = state.accept(action);
            statusId = state.getStatusId();

            if (DEBUG) {
                state.print(System.out);
            }
        }

        GameResult result = new GameResult(state);

        valuedTileAdder.accept(result);
        player.accept(result);

        return result;
    }
}
