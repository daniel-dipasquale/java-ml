package com.dipasquale.simulation.tictactoe.encoding;

import com.dipasquale.ai.common.NeuralNetworkEncoder;
import com.dipasquale.common.bit.VectorManipulatorSupport;
import com.dipasquale.simulation.tictactoe.GameState;
import lombok.Builder;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Builder
public final class InputPerPlayerInputNeuralNetworkEncoder implements NeuralNetworkEncoder<GameState> {
    private static final VectorManipulatorSupport VECTOR_MANIPULATOR_SUPPORT = VectorManipulatorSupport.create(2);
    private final int perspectiveParticipantId;
    private final VectorEncodingType vectorEncodingType;

    @Override
    public float[] encode(final GameState input) {
        int vector1 = 0;
        int vector2 = 0;

        for (int i = 0; i < GameState.BOARD_VECTOR_SIZE; i++) {
            int participantId = input.getParticipantId(i);

            if (participantId == perspectiveParticipantId) {
                vector1 = VECTOR_MANIPULATOR_SUPPORT.merge(vector1, i, 1);
            } else if (participantId != GameState.NO_PARTICIPANT_ID) {
                vector2 = VECTOR_MANIPULATOR_SUPPORT.merge(vector2, i, 1);
            }
        }

        return new float[]{vectorEncodingType.encode(vector1), vectorEncodingType.encode(vector2)};
    }
}
