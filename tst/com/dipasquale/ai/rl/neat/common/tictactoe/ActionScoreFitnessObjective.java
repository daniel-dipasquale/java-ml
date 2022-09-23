package com.dipasquale.ai.rl.neat.common.tictactoe;

import com.dipasquale.ai.rl.neat.ContestedNeatEnvironment;
import com.dipasquale.ai.rl.neat.SecludedNeatEnvironment;
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
                {0.8187333f, 0.40472585f, 0.84372634f, 0.3450493f, 1.0831424f, 0.30593848f, 0.84611243f, 0.3627812f, 0.84338164f},
                {-0.50775903f, -0.8455282f, -0.5087163f, -0.83390623f, -0.15126844f, -0.8271154f, -0.5224945f, -0.8175632f, -0.5162254f},
                {0.96744484f, 0.729807f, 0.9556548f, 0.72463083f, 1.1796442f, 0.72959316f, 0.962324f, 0.7165004f, 0.96535337f},
                {-0.5191783f, -0.821561f, -0.50240505f, -0.81646293f, -0.14940107f, -0.80990356f, -0.50731575f, -0.8300357f, -0.51085675f},
                {0.9655921f, 0.72832245f, 0.9794821f, 0.7211577f, 1.1959304f, 0.71843874f, 0.9570952f, 0.72529316f, 0.96679085f},
                {-0.50189704f, -0.8029344f, -0.4982052f, -0.80758446f, -0.14689524f, -0.80584824f, -0.5096584f, -0.8090906f, -0.5057607f},
                {1.0226603f, 0.75950253f, 1.0152097f, 0.7542408f, 1.2401692f, 0.75279504f, 1.0122758f, 0.75062996f, 1.0095878f},
                {-0.2640452f, -0.5957712f, -0.24970795f, -0.60293764f, 0.1167173f, -0.5964941f, -0.25778556f, -0.59462315f, -0.2597562f},
                {1.8264356f, 1.737912f, 1.8301092f, 1.7352599f, 1.8623135f, 1.7359272f, 1.8236066f, 1.7403543f, 1.8231554f}
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
        List<Integer> locationIds = result.getLocationIds();
        float player1ActionScore = 0f;
        int player1ActionCount = 0;
        float player2ActionScore = 0f;
        int player2ActionCount = 0;

        for (int i = 0, c = locationIds.size(); i < c; i++) {
            float actionScore = ACTION_SCORE_TABLE[i][locationIds.get(i)];

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

    public static SecludedNeatEnvironment createIsolatedEnvironment(final GameSupport gameSupport) {
        return new InternalSecludedEnvironment(gameSupport);
    }

    public static ContestedNeatEnvironment createContestedEnvironment(final GameSupport gameSupport) {
        return new InternalContestedEnvironment(gameSupport);
    }

    public static RewardHeuristic<GameAction, GameState> createValueHeuristic(final boolean subtractNextParticipant) {
        return new InternalRewardHeuristic(subtractNextParticipant);
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class InternalSecludedEnvironment implements SecludedNeatEnvironment {
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
    private static final class InternalContestedEnvironment implements ContestedNeatEnvironment {
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

        private static float calculateScore(final int offset, final List<Integer> locationIds) {
            float actionScore = 0f;
            int actionCount = 0;

            for (int i = offset, c = locationIds.size(); i < c; i += 2) {
                int locationId = locationIds.get(i);

                actionScore += ACTION_SCORE_TABLE[i][locationId];
                actionCount++;
            }

            if (actionCount == 0) {
                return 0f;
            }

            return actionScore / (float) actionCount;
        }

        @Override
        public float estimate(final GameState state) {
            List<Integer> locationIds = state.getLocationIds();
            int participantIdOffset = state.getParticipantId() - 1;
            float participantScore = calculateScore(participantIdOffset, locationIds);

            if (!subtractNextParticipant) {
                return RewardHeuristic.convertProbability(participantScore);
            }

            int nextParticipantIdOffset = state.getNextParticipantId() - 1;
            float nextParticipantScore = calculateScore(nextParticipantIdOffset, locationIds);

            return RewardHeuristic.convertProbability(participantScore - nextParticipantScore);
        }
    }
}
