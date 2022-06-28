package com.dipasquale.simulation.game2048;

import com.dipasquale.common.ArgumentValidatorSupport;
import com.dipasquale.common.factory.ObjectFactory;
import com.dipasquale.search.mcts.Environment;
import com.dipasquale.search.mcts.SearchResult;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.function.Consumer;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class Game {
    public static final float PROBABILITY_OF_SPAWNING_2 = ValuedTileSupport.PROBABILITY_OF_SPAWNING_2;
    public static final int BOARD_ONE_DIMENSION_LENGTH = Board.DIMENSION_VECTOR_LENGTH;
    public static final int BOARD_SQUARE_LENGTH = Board.VECTOR_LENGTH;
    private final ObjectFactory<GameState> initialStateFactory;
    private final Player valuedTileAdder;
    private final Consumer<SearchResult<GameAction, GameState>> inspector;

    public static Game create(final int victoriousExponentialValue, final Player valuedTileAdder, final Consumer<SearchResult<GameAction, GameState>> inspector) {
        ArgumentValidatorSupport.ensureGreaterThanOrEqualToZero(victoriousExponentialValue, "victoriousExponentialValue");
        ArgumentValidatorSupport.ensureLessThanOrEqualTo(victoriousExponentialValue, ValuedTileSupport.MAXIMUM_EXPONENTIAL_VALUE_PER_TILE, "victoriousExponentialValue", String.format("the limit is %d", ValuedTileSupport.MAXIMUM_EXPONENTIAL_VALUE_PER_TILE));

        ObjectFactory<GameState> initialStateFactory = () -> new GameState(victoriousExponentialValue);

        return new Game(initialStateFactory, valuedTileAdder, inspector);
    }

    public static Game create(final int victoriousExponentialValue, final Player valuedTileAdder) {
        return create(victoriousExponentialValue, valuedTileAdder, null);
    }

    public static int toDisplayValue(final int value) {
        return ValuedTileSupport.toDisplayValue(value);
    }

    public static int toExponentialValue(final int displayValue) {
        return ValuedTileSupport.toExponentialValue(displayValue);
    }

    public GameResult play(final Player player) {
        Player[] players = new Player[]{valuedTileAdder, player};
        Environment<GameAction, GameState, Player> environment = new Environment<>(initialStateFactory, players, inspector);
        GameState state = environment.interact();

        return new GameResult(state);
    }
}
