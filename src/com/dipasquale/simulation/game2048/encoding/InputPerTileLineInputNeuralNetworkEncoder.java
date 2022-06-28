package com.dipasquale.simulation.game2048.encoding;

import com.dipasquale.ai.common.NeuralNetworkEncoder;
import com.dipasquale.simulation.game2048.GameState;
import com.dipasquale.simulation.game2048.ValuedTileSupport;
import lombok.Builder;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Builder
public final class InputPerTileLineInputNeuralNetworkEncoder implements NeuralNetworkEncoder<GameState> {
    private final VectorEncodingType vectorEncodingType;

    @Override
    public float[] encode(final GameState input) {
        int[] inputs = new int[GameState.BOARD_DIMENSION_LENGTH * 2];

        for (int x = 0; x < GameState.BOARD_DIMENSION_LENGTH; x++) {
            int row = 0;
            int column = 0;

            for (int y = 0; y < GameState.BOARD_DIMENSION_LENGTH; y++) {
                int rowTileId = x * GameState.BOARD_DIMENSION_LENGTH + y;
                int rowExponentialValue = input.getExponentialValue(rowTileId);
                int columnTileId = y * GameState.BOARD_DIMENSION_LENGTH + x;
                int columnExponentialValue = input.getExponentialValue(columnTileId);

                row = ValuedTileSupport.mergeExponentialValue(row, y, rowExponentialValue);
                column = ValuedTileSupport.mergeExponentialValue(column, y, columnExponentialValue);
            }

            inputs[x] = row;
            inputs[x + GameState.BOARD_DIMENSION_LENGTH] = column;
        }

        return vectorEncodingType.encode(inputs);
    }
}
