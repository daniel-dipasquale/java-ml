package com.dipasquale.ai.rl.neat.common.tictactoe;

import com.dipasquale.ai.rl.neat.ContestNeatEnvironment;
import com.dipasquale.ai.rl.neat.IsolatedNeatEnvironment;
import com.dipasquale.ai.rl.neat.phenotype.GenomeActivator;
import com.dipasquale.search.mcts.SearchNode;
import com.dipasquale.search.mcts.alphazero.AlphaZeroEdge;
import com.dipasquale.search.mcts.alphazero.AlphaZeroValueCalculator;
import com.dipasquale.simulation.tictactoe.Game;
import com.dipasquale.simulation.tictactoe.GameAction;
import com.dipasquale.simulation.tictactoe.GameResult;
import com.dipasquale.simulation.tictactoe.GameState;
import com.dipasquale.simulation.tictactoe.Player;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
final class ActionScoreFitnessObjective {
    private static final float[][] ACTION_SCORE_TABLE = createActionScoreTable();

    private static float calculateProportionalValue(final float value, final float minimum, final float maximum) {
        return (value - minimum) / (maximum - minimum);
    }

    private static float[][] createActionScoreTable() {
        float[][] actionScores = { // x: move, y: location
                {0.24361748f, 0.13706407f, 0.24361748f, 0.13706407f, 0.3877551f, 0.13706407f, 0.24361748f, 0.13706407f, 0.24361748f},
                {-0.16081564f, -0.3153153f, -0.16081564f, -0.3153153f, 0.012366035f, -0.3153153f, -0.16081564f, -0.3153153f, -0.16081564f},
                {0.24361748f, 0.13706407f, 0.24361748f, 0.13706407f, 0.3877551f, 0.13706407f, 0.24361748f, 0.13706407f, 0.24361748f},
                {-0.16081564f, -0.3153153f, -0.16081564f, -0.3153153f, 0.012366035f, -0.3153153f, -0.16081564f, -0.3153153f, -0.16081564f},
                {0.24361748f, 0.13706407f, 0.24361748f, 0.13706407f, 0.3877551f, 0.13706407f, 0.24361748f, 0.13706407f, 0.24361748f},
                {-0.15638208f, -0.31088084f, -0.15638208f, -0.31088084f, 0.01655629f, -0.31088084f, -0.15638208f, -0.31088084f, -0.15638208f},
                {0.2736842f, 0.13846155f, 0.2736842f, 0.13846155f, 0.43783784f, 0.13846155f, 0.2736842f, 0.13846155f, 0.2736842f},
                {0.024539877f, -0.22463769f, 0.024539877f, -0.22463769f, 0.23404256f, -0.22463769f, 0.024539877f, -0.22463769f, 0.024539877f},
                {0.6785714f, 0.5f, 0.6785714f, 0.5f, 0.7894737f, 0.5f, 0.6785714f, 0.5f, 0.6785714f}
        };

        float[][] actionScoresFixed = new float[actionScores.length][actionScores[0].length];

        for (int i1 = 0, c1 = actionScores.length, c2 = actionScores[0].length; i1 < c1; i1++) {
            float minimum = Float.MAX_VALUE;
            float maximum = -Float.MAX_VALUE;

            for (int i2 = 0; i2 < c2; i2++) {
                float value = actionScores[i1][i2];

                if (Float.compare(value, minimum) < 0) {
                    minimum = value;
                }

                if (Float.compare(value, maximum) > 0) {
                    maximum = value;
                }
            }

            for (int i2 = 0; i2 < c2; i2++) {
                actionScoresFixed[i1][i2] = calculateProportionalValue(actionScores[i1][i2], minimum, maximum);
            }
        }

        return actionScoresFixed;
    }

    private static float[] play(final Player player1, final Player player2) {
        GameResult result = Game.play(player1, player2);
        int outcomeId = result.getOutcomeId();
        int[] actionIds = result.getActionIds();
        float player1ActionScore = 0f;
        int player1ActionCount = 0;
        float player2ActionScore = 0f;
        int player2ActionCount = 0;

        for (int i = 0; i < actionIds.length; i++) {
            float actionScore = ACTION_SCORE_TABLE[i][actionIds[i]];

            if (i % 2 == 0) {
                player1ActionScore += actionScore;
                player1ActionCount++;
            } else {
                player2ActionScore += actionScore;
                player2ActionCount++;
            }
        }

        float player1ActionCountFixed = (float) player1ActionCount;
        float player2ActionCountFixed = (float) player2ActionCount;

        player1ActionScore /= player1ActionCountFixed;
        player2ActionScore /= player2ActionCountFixed;

        return switch (outcomeId) {
            case 0 -> new float[]{3f + player1ActionScore, player2ActionScore};

            case 1 -> new float[]{player1ActionScore, 6f + player2ActionScore};

            default -> new float[]{1f + player1ActionScore, 2f + player2ActionScore};
        };
    }

    public static IsolatedNeatEnvironment createIsolatedEnvironment(final GameSupport gameSupport) {
        return new InternalIsolatedEnvironment(gameSupport);
    }

    public static ContestNeatEnvironment createContestedEnvironment(final GameSupport gameSupport) {
        return new InternalContestedEnvironment(gameSupport);
    }

    public static AlphaZeroValueCalculator<GameAction, GameState> createValueCalculator(final boolean subtractNextParticipant) {
        return new InternalValueCalculator(subtractNextParticipant);
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class InternalIsolatedEnvironment implements IsolatedNeatEnvironment {
        @Serial
        private static final long serialVersionUID = -4235097951450531213L;
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
    private static final class InternalContestedEnvironment implements ContestNeatEnvironment {
        @Serial
        private static final long serialVersionUID = 6796527022113256939L;
        private final GameSupport gameSupport;

        @Override
        public float[] test(final List<GenomeActivator> genomeActivators, final int round) {
            Player player1 = gameSupport.createPlayer(genomeActivators.get(0));
            Player player2 = gameSupport.createPlayer(genomeActivators.get(1));

            return play(player1, player2);
        }
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class InternalValueCalculator implements AlphaZeroValueCalculator<GameAction, GameState> {
        private final boolean subtractNextParticipant;

        private static float calculate(final int offset, final int[] actionIds) {
            float actionScore = 0f;
            int actionCount = 0;

            for (int i = offset; i < actionIds.length; i += 2) {
                int actionId = actionIds[i];

                actionScore += ACTION_SCORE_TABLE[i][actionId];
                actionCount++;
            }

            if (actionCount == 0) {
                return 0f;
            }

            return actionScore / (float) actionCount;
        }

        @Override
        public float calculate(final SearchNode<GameAction, AlphaZeroEdge, GameState> node) {
            GameState state = node.getState();
            int[] actionIds = state.replicateActionIds();
            int currentParticipantOffset = node.getAction().getParticipantId() - 1;
            float currentParticipantValue = calculate(currentParticipantOffset, actionIds);

            if (!subtractNextParticipant) {
                return currentParticipantValue * 2f - 1f;
            }

            int nextParticipantOffset = state.getNextParticipantId() - 1;
            float nextParticipantValue = calculate(nextParticipantOffset, actionIds);

            return (currentParticipantValue - nextParticipantValue) * 2f - 1f;
        }
    }
}
