package com.dipasquale.simulation.tictactoe;

import com.dipasquale.search.mcts.Environment;
import com.dipasquale.search.mcts.MonteCarloTreeSearch;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class Game {
    private static GameResult createResult(final int outcomeId, final GameState state) {
        return new GameResult(outcomeId, state.replicateActionIds());
    }

    public static GameResult play(final Player player1, final Player player2) {
        Player[] players = new Player[]{player1, player2};
        Environment<GameAction, GameState, Player> environment = new Environment<>(GameState::new, players);
        GameState state = environment.interact();
        int statusId = state.getStatusId();

        if (statusId == MonteCarloTreeSearch.DRAWN_STATUS_ID) {
            return createResult(GameResult.DRAWN_OUTCOME_ID, state);
        }

        int outcomeId = statusId - 1;

        return createResult(outcomeId, state);
    }
}
