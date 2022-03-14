package com.dipasquale.simulation.tictactoe;

import com.dipasquale.ai.common.NeuralNetworkEncoder;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public final class ValuePerTileInputNeuralNetworkEncoder implements NeuralNetworkEncoder<GameState> {
    private final int perspectiveParticipantId;

    @Override
    public float[] encode(final GameState input) {
        float[] encodedInput = new float[GameState.BOARD_LENGTH];

        for (int i = 0; i < GameState.BOARD_LENGTH; i++) {
            int participantId = input.getActionOwnerParticipantId(i);

            if (participantId == perspectiveParticipantId) {
                encodedInput[i] = 1f;
            } else if (participantId != GameState.NO_PARTICIPANT_ID) {
                encodedInput[i] = -1f;
            }
        }

        return encodedInput;
    }
}
