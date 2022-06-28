package com.dipasquale.simulation.tictactoe.encoding;

import com.dipasquale.ai.common.NeuralNetworkEncoder;
import com.dipasquale.common.bit.VectorManipulatorSupport;
import com.dipasquale.simulation.tictactoe.GameState;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public final class InputPerBoardInputNeuralNetworkEncoder implements NeuralNetworkEncoder<GameState> {
    private static final VectorManipulatorSupport BIT_MANIPULATOR_SUPPORT = VectorManipulatorSupport.create(2);
    private final int perspectiveParticipantId;
    private final VectorEncodingType vectorEncodingType;

    @Override
    public float[] encode(final GameState input) {
        int vector = 0;

        for (int i = 0; i < GameState.BOARD_VECTOR_SIZE; i++) {
            int participantId = input.getParticipantId(i);

            if (participantId == perspectiveParticipantId) {
                vector = BIT_MANIPULATOR_SUPPORT.merge(vector, i, 1);
            } else if (participantId != GameState.NO_PARTICIPANT_ID) {
                vector = BIT_MANIPULATOR_SUPPORT.merge(vector, i, 2);
            }
        }

        return new float[]{vectorEncodingType.encode(vector)};
    }
}
