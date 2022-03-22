package com.dipasquale.ai.rl.neat.common.game2048;

import com.dipasquale.ai.rl.neat.IsolatedNeatEnvironment;
import com.dipasquale.ai.rl.neat.phenotype.GenomeActivator;
import com.dipasquale.search.mcts.common.ValueHeuristic;
import com.dipasquale.simulation.game2048.Game;
import com.dipasquale.simulation.game2048.GameAction;
import com.dipasquale.simulation.game2048.GameResult;
import com.dipasquale.simulation.game2048.GameState;
import com.dipasquale.simulation.game2048.Player;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.io.Serial;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
final class AverageValuedTileObjective {
    private static final int DIMENSION = 4;
    private static final int LENGTH = DIMENSION * DIMENSION;

    public static <T extends GameAction> IsolatedNeatEnvironment createEnvironment(final RandomOutcomeGameSupport gameSupport) {
        return new InternalEnvironment(gameSupport);
    }

    public static ValueHeuristic<GameAction, GameState> createValueHeuristic() {
        return new InternalValueHeuristic();
    }

    private static float calculateRate(final double total, final double valuedTileCount) {
        return (float) (total / valuedTileCount);
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class InternalEnvironment implements IsolatedNeatEnvironment {
        @Serial
        private static final long serialVersionUID = -4572508371867439103L;
        private final RandomOutcomeGameSupport gameSupport;

        @Override
        public float test(final GenomeActivator genomeActivator) {
            Player player = gameSupport.createPlayer(genomeActivator);
            Game game = gameSupport.createGame();
            GameResult result = game.play(player);
            double total = 0D;

            for (int tileId = 0; tileId < LENGTH; tileId++) {
                int value = result.getValueInTile(tileId);

                if (value > 0) {
                    total += Math.pow(2D, value);
                }
            }

            return calculateRate(total, result.getValuedTileCount());
        }
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class InternalValueHeuristic implements ValueHeuristic<GameAction, GameState> {
        @Override
        public float estimate(final GameState state) {
            double total = 0D;

            for (int tileId = 0; tileId < LENGTH; tileId++) {
                int value = state.getValueInTile(tileId);

                if (value > 0) {
                    total += Math.pow(2D, value);
                }
            }

            float rate = calculateRate(total, state.getValuedTileCount());

            return ValueHeuristic.calculateUnbounded(rate);
        }
    }
}
