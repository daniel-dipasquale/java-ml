package com.dipasquale.ai.rl.neat.common.game2048;

import com.dipasquale.ai.rl.neat.SecludedNeatEnvironment;
import com.dipasquale.ai.rl.neat.phenotype.GenomeActivator;
import com.dipasquale.simulation.game2048.Game;
import com.dipasquale.simulation.game2048.GameResult;
import com.dipasquale.simulation.game2048.Player;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.io.Serial;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class ScoreByValuedTileCountEnvironment implements SecludedNeatEnvironment {
    @Serial
    private static final long serialVersionUID = -1848737743365320805L;
    private final RandomOutcomeGameSupport gameSupport;

    @Override
    public float test(final GenomeActivator genomeActivator) {
        Player player = gameSupport.createPlayer(genomeActivator);
        Game game = gameSupport.createGame();
        GameResult result = game.play(player);
        float score = (float) result.getScore();
        float valuedTileCount = (float) result.getValuedTileCount();

        return score / valuedTileCount;
    }
}
