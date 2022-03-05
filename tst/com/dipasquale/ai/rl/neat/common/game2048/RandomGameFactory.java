package com.dipasquale.ai.rl.neat.common.game2048;

import com.dipasquale.common.factory.ObjectFactory;
import com.dipasquale.common.random.float1.UniformRandomSupport;
import com.dipasquale.simulation.game2048.Game;
import com.dipasquale.simulation.game2048.ValuedTileSupport;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class RandomGameFactory implements ObjectFactory<Game> {
    private final int maximumValue;

    @Override
    public Game create() {
        UniformRandomSupport locationRandomSupport = new UniformRandomSupport();
        UniformRandomSupport valueRandomSupport = new UniformRandomSupport();
        ValuedTileSupport valuedTileSupport = new ValuedTileSupport(locationRandomSupport, valueRandomSupport);

        return new Game(valuedTileSupport, maximumValue);
    }
}
