package com.dipasquale.simulation.tictactoe;

import com.dipasquale.ai.rl.neat.core.NeatEncoder;
import com.dipasquale.common.bit.int1.BitManipulatorSupport;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public final class SingleInputNeatEncoder implements NeatEncoder<GameEnvironment> {
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
        int result = 0;

        for (int i = 0; i < board.length; i++) {
            int play = board[i];

            if (play == perspectiveParticipantId) {
                result = BIT_MANIPULATOR_SUPPORT.setAndGet(result, i, 1);
            } else if (play != 0) {
                result = BIT_MANIPULATOR_SUPPORT.setAndGet(result, i, 2);
            }
        }

        return new float[]{encode(result)};
    }
}
