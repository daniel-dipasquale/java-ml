package com.dipasquale.simulation.tictactoe;

import com.dipasquale.search.mcts.core.MonteCarloTreeSearch;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Game {
    public static int play(final Player player1, final Player player2) {
        Player[] players = new Player[]{player1, player2};
        GameEnvironment environment = new GameEnvironment();
        int statusId = environment.getStatusId();

        for (int i = 0; statusId == MonteCarloTreeSearch.IN_PROGRESS; i = (i + 1) % players.length) {
            GameState state = players[i].createNextState(environment);

            environment = environment.accept(state);
            statusId = environment.getStatusId();
        }

        if (statusId == MonteCarloTreeSearch.DRAWN) {
            return statusId;
        }

        return statusId - 1;
    }
}
