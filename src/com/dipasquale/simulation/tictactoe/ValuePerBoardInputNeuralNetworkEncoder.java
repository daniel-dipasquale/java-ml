package com.dipasquale.simulation.tictactoe;

import com.dipasquale.ai.common.NeuralNetworkEncoder;
import com.dipasquale.common.bit.int1.BitManipulatorSupport;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public final class ValuePerBoardInputNeuralNetworkEncoder implements NeuralNetworkEncoder<GameState> {
    private static final BitManipulatorSupport BIT_MANIPULATOR_SUPPORT = BitManipulatorSupport.create(2);
    private final int perspectiveParticipantId;
    private final VectorEncodingType vectorEncodingType;

    @Override
    public float[] encode(final GameState input) {
        int result = 0;

        for (int i = 0; i < GameState.BOARD_LENGTH; i++) {
            int participantId = input.getActionOwnerParticipantId(i);

            if (participantId == perspectiveParticipantId) {
                result = BIT_MANIPULATOR_SUPPORT.merge(result, i, 1);
            } else if (participantId != GameState.NO_PARTICIPANT_ID) {
                result = BIT_MANIPULATOR_SUPPORT.merge(result, i, 2);
            }
        }

        return new float[]{vectorEncodingType.encode(result)};
    }
}
