package com.dipasquale.ai.rl.neat.common.game2048;

import com.dipasquale.common.factory.ObjectFactory;
import com.dipasquale.common.random.UniformRandomSupport;
import com.dipasquale.simulation.game2048.Game;
import com.dipasquale.simulation.game2048.ValuedTileSupport;
import com.dipasquale.simulation.game2048.player.ValuedTileAllocationPlayer;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class RandomGameFactory implements ObjectFactory<Game> {
    private final int victoryValue;

    @Override
    public Game create() {
        UniformRandomSupport tileIdRandomSupport = new UniformRandomSupport();
        UniformRandomSupport exponentialValueRandomSupport = new UniformRandomSupport();
        ValuedTileSupport valuedTileSupport = new ValuedTileSupport(tileIdRandomSupport, exponentialValueRandomSupport);
        ValuedTileAllocationPlayer valuedTileAdder = new ValuedTileAllocationPlayer(valuedTileSupport);

        return Game.create(victoryValue, valuedTileAdder);
    }
}
