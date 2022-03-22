package com.dipasquale.ai.rl.neat.common.game2048;

import com.dipasquale.common.factory.ObjectFactory;
import com.dipasquale.common.random.float1.UniformRandomSupport;
import com.dipasquale.simulation.game2048.Game;
import com.dipasquale.simulation.game2048.Player;
import com.dipasquale.simulation.game2048.ValuedTileSupport;

final class RandomGameFactory implements ObjectFactory<Game> {
    private final int victoryValue;
    private final Player valuedTileAdder;

    RandomGameFactory(final int maximumValue, final Player valuedTileAdder) {
        this.victoryValue = (int) (Math.log(maximumValue) / Math.log(2D));
        this.valuedTileAdder = valuedTileAdder;
    }

    @Override
    public Game create() {
        UniformRandomSupport locationRandomSupport = new UniformRandomSupport();
        UniformRandomSupport valueRandomSupport = new UniformRandomSupport();
        ValuedTileSupport valuedTileSupport = new ValuedTileSupport(locationRandomSupport, valueRandomSupport);

        return new Game(valuedTileSupport, victoryValue, valuedTileAdder);
    }
}
