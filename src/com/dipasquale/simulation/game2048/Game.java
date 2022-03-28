package com.dipasquale.simulation.game2048;

import com.dipasquale.common.factory.ObjectFactory;
import com.dipasquale.search.mcts.Environment;

public final class Game {
    public static final float PROBABILITY_OF_SPAWNING_2 = GameState.PROBABILITY_OF_SPAWNING_2;
    public static final int BOARD_ONE_DIMENSION_LENGTH = Board.ONE_DIMENSION_LENGTH;
    public static final int BOARD_SQUARE_LENGTH = Board.SQUARE_LENGTH;
    private final ObjectFactory<GameState> initialGameFactory;
    private final Player valuedTileAdder;

    public Game(final ValuedTileSupport valuedTileSupport, final int victoryValue, final Player valuedTileAdder) {
        this.initialGameFactory = () -> new GameState(valuedTileSupport, victoryValue);
        this.valuedTileAdder = valuedTileAdder;
    }

    public static int getDisplayValue(final int value) {
        return Board.getDisplayValue(value);
    }

    public GameResult play(final Player player) {
        Player[] players = new Player[]{valuedTileAdder, player};
        Environment<GameAction, GameState, Player> environment = new Environment<>(initialGameFactory, players);
        GameState state = environment.interact();

        return new GameResult(state);
    }
}
