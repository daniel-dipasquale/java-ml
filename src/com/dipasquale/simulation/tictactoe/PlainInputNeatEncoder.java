package com.dipasquale.simulation.tictactoe;

import com.dipasquale.ai.rl.neat.core.NeatEncoder;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public final class PlainInputNeatEncoder implements NeatEncoder<GameState> {
    private final int perspectiveParticipantId;

    @Override
    public float[] encode(final GameState input) {
        int[] board = input.getBoard();
        float[] encodedInput = new float[9];

        for (int i = 0; i < board.length; i++) {
            int play = board[i];

            if (play == perspectiveParticipantId) {
                encodedInput[i] = 1f;
            } else if (play != 0) {
                encodedInput[i] = -1f;
            }
        }

        return encodedInput;
    }
}
