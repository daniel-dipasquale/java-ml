package com.dipasquale.ai.rl.neat.common.tictactoe;

import com.dipasquale.ai.rl.neat.ContestNeatEnvironment;
import com.dipasquale.ai.rl.neat.phenotype.GenomeActivator;
import com.dipasquale.simulation.tictactoe.Game;
import com.dipasquale.simulation.tictactoe.GameResult;
import com.dipasquale.simulation.tictactoe.Player;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.util.List;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class WinOrDrawEnvironment implements ContestNeatEnvironment {
    @Serial
    private static final long serialVersionUID = -5306025686022553605L;
    private final GameSupport gameSupport;

    @Override
    public float[] test(final List<GenomeActivator> genomeActivators, final int round) {
        Player player1 = gameSupport.createPlayer(genomeActivators.get(0));
        Player player2 = gameSupport.createPlayer(genomeActivators.get(1));
        GameResult result = Game.play(player1, player2);
        int outcomeId = result.getOutcomeId();

        if (outcomeId == 0) {
            return new float[]{3f, 0f};
        }

        if (outcomeId == 1) {
            return new float[]{0f, 3f};
        }

        return new float[]{1f, 1f};
    }
}
