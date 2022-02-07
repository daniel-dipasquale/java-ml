package com.dipasquale.simulation.tictactoe;

import com.dipasquale.search.mcts.core.MonteCarloTreeSearch;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Game {
    private static GameResult createResult(final int outcomeId, final GameState state) {
        List<Integer> moves = Collections.unmodifiableList(state.getMoves());

        return new GameResult(outcomeId, moves);
    }

    public static GameResult play(final Player player1, final Player player2) {
        Player[] players = new Player[]{player1, player2};
        GameState state = new GameState();
        int statusId = state.getStatusId();

        for (int i = 0; statusId == MonteCarloTreeSearch.IN_PROGRESS; i = (i + 1) % players.length) {
            GameAction action = players[i].createNextAction(state);

            state = state.accept(action);
            statusId = state.getStatusId();
        }

        if (statusId == MonteCarloTreeSearch.DRAWN) {
            return createResult(statusId, state);
        }

        return createResult(statusId - 1, state);
    }
}
