package com.dipasquale.simulation.tictactoe;

import com.dipasquale.ai.rl.neat.core.NeatEncoder;
import com.dipasquale.common.bit.int1.BitManipulatorSupport;
import lombok.Builder;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Builder
public final class DoubleInputNeatEncoder implements NeatEncoder<GameEnvironment> {
    private static final BitManipulatorSupport BIT_MANIPULATOR_SUPPORT = BitManipulatorSupport.create(2);
    private final int perspectiveParticipantId;
    private final boolean vectorFormatEnabled;

    private float encode(final int result) {
        if (!vectorFormatEnabled) {
            return (float) result;
        }

        return Float.intBitsToFloat(result);
    }

    @Override
    public float[] encode(final GameEnvironment input) {
        int[] board = input.getBoard();
        int result1 = 0;
        int result2 = 0;

        for (int i = 0; i < board.length; i++) {
            int play = board[i];

            if (play == perspectiveParticipantId) {
                result1 = BIT_MANIPULATOR_SUPPORT.merge(result1, i, 1);
            } else if (play != 0) {
                result2 = BIT_MANIPULATOR_SUPPORT.merge(result2, i, 1);
            }
        }

        return new float[]{encode(result1), encode(result2)};
    }
}
