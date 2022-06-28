package com.dipasquale.simulation.tictactoe.encoding;

import com.dipasquale.ai.common.NeuralNetworkEncoder;
import com.dipasquale.simulation.tictactoe.GameState;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public final class InputPerTileInputNeuralNetworkEncoder implements NeuralNetworkEncoder<GameState> {
    private final int perspectiveParticipantId;

    @Override
    public float[] encode(final GameState input) {
        float[] encodedInput = new float[GameState.BOARD_VECTOR_SIZE];

        for (int i = 0; i < GameState.BOARD_VECTOR_SIZE; i++) {
            int participantId = input.getParticipantId(i);

            if (participantId == perspectiveParticipantId) {
                encodedInput[i] = 1f;
            } else if (participantId != GameState.NO_PARTICIPANT_ID) {
                encodedInput[i] = -1f;
            }
        }

        return encodedInput;
    }
}
