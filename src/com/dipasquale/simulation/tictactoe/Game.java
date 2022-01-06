package com.dipasquale.simulation.tictactoe;

import com.dipasquale.search.mcts.core.MonteCarloTreeSearch;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Game {
    private static GameResult create(final int outcomeId, final GameEnvironment environment) {
        List<Integer> moves = Collections.unmodifiableList(environment.getMoves());

        return new GameResult(outcomeId, moves);
    }

    public static GameResult play(final Player player1, final Player player2) {
        Player[] players = new Player[]{player1, player2};
        GameEnvironment environment = new GameEnvironment();
        int statusId = environment.getStatusId();

        for (int i = 0; statusId == MonteCarloTreeSearch.IN_PROGRESS; i = (i + 1) % players.length) {
            GameState state = players[i].createNextState(environment);

            environment = environment.accept(state);
            statusId = environment.getStatusId();
        }

        if (statusId == MonteCarloTreeSearch.DRAWN) {
            return create(statusId, environment);
        }

        return create(statusId - 1, environment);
    }
}
