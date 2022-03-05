package com.dipasquale.simulation.game2048;

import com.dipasquale.ai.common.NeuralNetworkEncoder;
import com.dipasquale.common.bit.int1.BitManipulatorSupport;
import lombok.Builder;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Builder
public final class ValuePerRowNeuralNetworkEncoder implements NeuralNetworkEncoder<GameState> {
    private static final BitManipulatorSupport BIT_MANIPULATOR_SUPPORT = BitManipulatorSupport.create(4);
    private final VectorEncodingType vectorEncodingType;

    @Override
    public float[] encode(final GameState input) {
        int[] results = new int[Board.DIMENSION];

        for (int x = 0; x < Board.DIMENSION; x++) {
            int row = 0;

            for (int y = 0; y < Board.DIMENSION; y++) {
                int tileId = x * Board.DIMENSION + y;
                int value = input.getValueFromTile(tileId);

                row = BIT_MANIPULATOR_SUPPORT.merge(row, y, value);
            }

            results[x] = row;
        }

        return vectorEncodingType.encode(results);
    }
}
