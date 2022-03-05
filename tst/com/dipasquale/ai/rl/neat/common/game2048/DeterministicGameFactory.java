package com.dipasquale.ai.rl.neat.common.game2048;

import com.dipasquale.common.factory.ObjectFactory;
import com.dipasquale.common.random.float1.DeterministicRandomSupport;
import com.dipasquale.simulation.game2048.Game;
import com.dipasquale.simulation.game2048.GameAction;
import com.dipasquale.simulation.game2048.ValuedTileSupport;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class DeterministicGameFactory<T extends GameAction> implements ObjectFactory<Game> {
    private final int locationSize;
    private final int valueSize;
    private final int maximumValue;

    @Override
    public Game create() {
        DeterministicRandomSupport locationRandomSupport = new DeterministicRandomSupport(locationSize);
        DeterministicRandomSupport valueRandomSupport = new DeterministicRandomSupport(valueSize);
        ValuedTileSupport valuedTileSupport = new ValuedTileSupport(locationRandomSupport, valueRandomSupport);

        return new Game(valuedTileSupport, maximumValue);
    }
}
