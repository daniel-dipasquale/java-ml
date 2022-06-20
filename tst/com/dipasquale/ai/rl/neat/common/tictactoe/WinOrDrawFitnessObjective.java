package com.dipasquale.ai.rl.neat.common.tictactoe;

import com.dipasquale.ai.rl.neat.ContestedNeatEnvironment;
import com.dipasquale.ai.rl.neat.IsolatedNeatEnvironment;
import com.dipasquale.ai.rl.neat.phenotype.GenomeActivator;
import com.dipasquale.simulation.tictactoe.Game;
import com.dipasquale.simulation.tictactoe.GameResult;
import com.dipasquale.simulation.tictactoe.Player;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.util.List;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
final class WinOrDrawFitnessObjective {
    private static float[] play(final Player player1, final Player player2) {
        GameResult result = Game.play(player1, player2);
        int outcomeId = result.getOutcomeId();

        return switch (outcomeId) {
            case 0 -> new float[]{3f, 0f};

            case 1 -> new float[]{0f, 6f};

            default -> new float[]{1f, 2f};
        };
    }

    public static IsolatedNeatEnvironment createIsolatedEnvironment(final GameSupport gameSupport) {
        return new InternalIsolatedEnvironment(gameSupport);
    }

    public static ContestedNeatEnvironment createContestedEnvironment(final GameSupport gameSupport) {
        return new InternalContestedEnvironment(gameSupport);
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class InternalIsolatedEnvironment implements IsolatedNeatEnvironment {
        @Serial
        private static final long serialVersionUID = -4303134055956916075L;
        private final GameSupport gameSupport;

        @Override
        public float test(final GenomeActivator genomeActivator) {
            Player aiPlayer = gameSupport.createPlayer(genomeActivator);
            Player basicPlayer = gameSupport.createClassicPlayer();
            float[] scores1 = play(aiPlayer, basicPlayer);
            float[] scores2 = play(basicPlayer, aiPlayer);

            return scores1[0] + scores2[1];
        }
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class InternalContestedEnvironment implements ContestedNeatEnvironment {
        @Serial
        private static final long serialVersionUID = -5504682570162422449L;
        private final GameSupport gameSupport;

        @Override
        public float[] test(final List<GenomeActivator> genomeActivators, final int round) {
            Player player1 = gameSupport.createPlayer(genomeActivators.get(0));
            Player player2 = gameSupport.createPlayer(genomeActivators.get(1));

            return play(player1, player2);
        }
    }
}
