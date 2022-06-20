package com.dipasquale.ai.rl.neat.common.tictactoe;

import com.dipasquale.ai.rl.neat.ConfinedNeatEnvironment;
import com.dipasquale.ai.rl.neat.ContestNeatEnvironment;
import com.dipasquale.ai.rl.neat.phenotype.GenomeActivator;
import com.dipasquale.search.mcts.heuristic.selection.RewardHeuristic;
import com.dipasquale.simulation.tictactoe.Game;
import com.dipasquale.simulation.tictactoe.GameAction;
import com.dipasquale.simulation.tictactoe.GameResult;
import com.dipasquale.simulation.tictactoe.GameState;
import com.dipasquale.simulation.tictactoe.Player;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.util.List;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
final class ActionScoreFitnessObjective {
    private static final float[][] ACTION_SCORE_TABLE = createActionScoreTable();

    private static float calculateProportionalValue(final float value, final float minimum, final float maximum) {
        return (value - minimum) / (maximum - minimum);
    }

    private static float[][] createActionScoreTable() {
        float[][] actionScores = { // x: move, y: location
                {0.67416704f, 0.44931063f, 0.67416704f, 0.44931063f, 0.9536178f, 0.44931063f, 0.67416704f, 0.44931063f, 0.67416704f},
                {-0.14504941f, -0.45045045f, -0.14504941f, -0.45045045f, 0.22258863f, -0.45045045f, -0.14504941f, -0.45045045f, -0.14504941f},
                {0.67416704f, 0.44931063f, 0.67416704f, 0.44931063f, 0.9536178f, 0.44931063f, 0.67416704f, 0.44931063f, 0.67416704f},
                {-0.14504941f, -0.45045045f, -0.14504941f, -0.45045045f, 0.22258863f, -0.45045045f, -0.14504941f, -0.45045045f, -0.14504941f},
                {0.67416704f, 0.44931063f, 0.67416704f, 0.44931063f, 0.9536178f, 0.44931063f, 0.67416704f, 0.44931063f, 0.67416704f},
                {-0.13524936f, -0.44041452f, -0.13524936f, -0.44041452f, 0.23178808f, -0.44041452f, -0.13524936f, -0.44041452f, -0.13524936f},
                {0.7368421f, 0.46153846f, 0.7368421f, 0.46153846f, 1.0486486f, 0.46153846f, 0.7368421f, 0.46153846f, 0.7368421f},
                {0.2638037f, -0.19565217f, 0.2638037f, -0.19565217f, 0.68085104f, -0.19565217f, 0.2638037f, -0.19565217f, 0.2638037f},
                {1.6785715f, 1.5f, 1.6785715f, 1.5f, 1.7894737f, 1.5f, 1.6785715f, 1.5f, 1.6785715f}
        };

        float[][] fixedActionScores = new float[actionScores.length][actionScores[0].length];

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
                fixedActionScores[i1][i2] = calculateProportionalValue(actionScores[i1][i2], minimum, maximum);
            }
        }

        return fixedActionScores;
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

        float fixedPlayer1ActionCount = (float) player1ActionCount;
        float fixedPlayer2ActionCount = (float) player2ActionCount;

        player1ActionScore /= fixedPlayer1ActionCount;
        player2ActionScore /= fixedPlayer2ActionCount;

        return switch (outcomeId) {
            case 0 -> new float[]{3f + player1ActionScore, player2ActionScore};

            case 1 -> new float[]{player1ActionScore, 6f + player2ActionScore};

            default -> new float[]{1f + player1ActionScore, 2f + player2ActionScore};
        };
    }

    public static ConfinedNeatEnvironment createConfinedEnvironment(final GameSupport gameSupport) {
        return new InternalConfinedEnvironment(gameSupport);
    }

    public static ContestNeatEnvironment createContestedEnvironment(final GameSupport gameSupport) {
        return new InternalContestedEnvironment(gameSupport);
    }

    public static RewardHeuristic<GameAction, GameState> createValueHeuristic(final boolean subtractNextParticipant) {
        return new InternalRewardHeuristic(subtractNextParticipant);
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class InternalConfinedEnvironment implements ConfinedNeatEnvironment {
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
    private static final class InternalRewardHeuristic implements RewardHeuristic<GameAction, GameState> {
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
        public float estimate(final GameState state) {
            int[] actionIds = state.replicateActionIds();
            int participantOffset = state.getParticipantId() - 1;
            float participantValue = calculate(participantOffset, actionIds);

            if (!subtractNextParticipant) {
                return RewardHeuristic.convertProbability(participantValue);
            }

            int nextParticipantOffset = state.getNextParticipantId() - 1;
            float nextParticipantValue = calculate(nextParticipantOffset, actionIds);

            return RewardHeuristic.convertProbability(participantValue - nextParticipantValue);
        }
    }
}
