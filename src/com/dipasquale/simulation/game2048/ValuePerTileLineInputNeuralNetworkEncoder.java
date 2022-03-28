package com.dipasquale.simulation.game2048;

import com.dipasquale.ai.common.NeuralNetworkEncoder;
import com.dipasquale.common.bit.int1.BitManipulatorSupport;
import lombok.Builder;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Builder
public final class ValuePerTileLineInputNeuralNetworkEncoder implements NeuralNetworkEncoder<GameState> {
    private static final BitManipulatorSupport BIT_MANIPULATOR_SUPPORT = BitManipulatorSupport.create(4);
    private final VectorEncodingType vectorEncodingType;

    @Override
    public float[] encode(final GameState input) {
        int[] results = new int[Board.ONE_DIMENSION_LENGTH * 2];

        for (int x = 0; x < Board.ONE_DIMENSION_LENGTH; x++) {
            int row = 0;
            int column = 0;

            for (int y = 0; y < Board.ONE_DIMENSION_LENGTH; y++) {
                int rowTileId = x * Board.ONE_DIMENSION_LENGTH + y;
                int rowValue = input.getValueInTile(rowTileId);
                int columnTileId = y * Board.ONE_DIMENSION_LENGTH + x;
                int columnValue = input.getValueInTile(columnTileId);

                row = BIT_MANIPULATOR_SUPPORT.merge(row, y, rowValue);
                column = BIT_MANIPULATOR_SUPPORT.merge(column, y, columnValue);
            }

            results[x] = row;
            results[x + Board.ONE_DIMENSION_LENGTH] = column;
        }

        return vectorEncodingType.encode(results);
    }
}
