package com.dipasquale.simulation.tictactoe;

import com.dipasquale.search.mcts.core.MonteCarloTreeSearch;

public final class Game {
    private final Player[] players;

    public Game(final Player player1, final Player player2) {
        this.players = new Player[]{player1, player2};
    }

    public int play() {
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
