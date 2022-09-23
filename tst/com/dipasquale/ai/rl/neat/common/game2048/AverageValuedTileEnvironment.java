package com.dipasquale.ai.rl.neat.common.game2048;

import com.dipasquale.ai.rl.neat.SecludedNeatEnvironment;
import com.dipasquale.ai.rl.neat.phenotype.GenomeActivator;
import com.dipasquale.simulation.game2048.Game;
import com.dipasquale.simulation.game2048.GameResult;
import com.dipasquale.simulation.game2048.Player;
import lombok.RequiredArgsConstructor;

import java.io.Serial;

@RequiredArgsConstructor
public final class AverageValuedTileEnvironment implements SecludedNeatEnvironment {
    @Serial
    private static final long serialVersionUID = -7587295098556560981L;
    private final RandomOutcomeGameSupport gameSupport;

    @Override
    public float test(final GenomeActivator genomeActivator) {
        Player player = gameSupport.createPlayer(genomeActivator);
        Game game = gameSupport.createGame();
        GameResult result = game.play(player);
        double total = 0D;

        for (int tileId = 0; tileId < Game.BOARD_SQUARE_LENGTH; tileId++) {
            int value = result.getValueInTile(tileId);

            if (value > 0) {
                total += Game.toDisplayValue(value);
            }
        }

        double average = total / (double) result.getValuedTileCount();

        return (float) average;
    }
}
