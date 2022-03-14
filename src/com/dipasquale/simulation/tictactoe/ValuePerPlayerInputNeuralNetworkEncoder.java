package com.dipasquale.simulation.tictactoe;

import com.dipasquale.ai.common.NeuralNetworkEncoder;
import com.dipasquale.common.bit.int1.BitManipulatorSupport;
import lombok.Builder;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Builder
public final class ValuePerPlayerInputNeuralNetworkEncoder implements NeuralNetworkEncoder<GameState> {
    private static final BitManipulatorSupport BIT_MANIPULATOR_SUPPORT = BitManipulatorSupport.create(2);
    private final int perspectiveParticipantId;
    private final VectorEncodingType vectorEncodingType;

    @Override
    public float[] encode(final GameState input) {
        int result1 = 0;
        int result2 = 0;

        for (int i = 0; i < GameState.BOARD_LENGTH; i++) {
            int participantId = input.getActionOwnerParticipantId(i);

            if (participantId == perspectiveParticipantId) {
                result1 = BIT_MANIPULATOR_SUPPORT.merge(result1, i, 1);
            } else if (participantId != GameState.NO_PARTICIPANT_ID) {
                result2 = BIT_MANIPULATOR_SUPPORT.merge(result2, i, 1);
            }
        }

        return new float[]{vectorEncodingType.encode(result1), vectorEncodingType.encode(result2)};
    }
}
