package com.dipasquale.simulation.tictactoe;

import com.dipasquale.search.mcts.MonteCarloTreeSearch;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Game {
    private static final boolean IS_SIMULATION = false;

    private static GameResult createResult(final int outcomeId, final GameState state) {
        return new GameResult(outcomeId, state.replicateActionIds());
    }

    private static GameResult playInternal(final Player player1, final Player player2) {
        Player[] players = new Player[]{player1, player2};
        GameState state = new GameState();
        int statusId = state.getStatusId();

        for (int i = 0; statusId == MonteCarloTreeSearch.IN_PROGRESS_STATUS_ID; i = (i + 1) % players.length) {
            GameAction action = players[i].createNextAction(state);

            state = state.accept(action, IS_SIMULATION);
            statusId = state.getStatusId();
        }

        if (statusId == MonteCarloTreeSearch.DRAWN_STATUS_ID) {
            return createResult(GameResult.DRAWN_OUTCOME_ID, state);
        }

        int outcomeId = statusId - 1;

        return createResult(outcomeId, state);
    }

    public static GameResult play(final Player player1, final Player player2) {
        GameResult result = playInternal(player1, player2);

        player1.accept(result);
        player2.accept(result);

        return result;
    }
}
