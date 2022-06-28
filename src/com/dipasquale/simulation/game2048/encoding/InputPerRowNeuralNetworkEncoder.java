package com.dipasquale.simulation.game2048.encoding;

import com.dipasquale.ai.common.NeuralNetworkEncoder;
import com.dipasquale.simulation.game2048.GameState;
import com.dipasquale.simulation.game2048.ValuedTileSupport;
import lombok.Builder;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Builder
public final class InputPerRowNeuralNetworkEncoder implements NeuralNetworkEncoder<GameState> {
    private final VectorEncodingType vectorEncodingType;

    @Override
    public float[] encode(final GameState input) {
        int[] inputs = new int[GameState.BOARD_DIMENSION_LENGTH];

        for (int x = 0; x < GameState.BOARD_DIMENSION_LENGTH; x++) {
            int row = 0;

            for (int y = 0; y < GameState.BOARD_DIMENSION_LENGTH; y++) {
                int tileId = x * GameState.BOARD_DIMENSION_LENGTH + y;
                int exponentialValue = input.getExponentialValue(tileId);

                row = ValuedTileSupport.mergeExponentialValue(row, y, exponentialValue);
            }

            inputs[x] = row;
        }

        return vectorEncodingType.encode(inputs);
    }
}
