package com.dipasquale.simulation.game2048.encoding;

import com.dipasquale.ai.common.NeuralNetworkEncoder;
import com.dipasquale.simulation.game2048.GameState;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class InputPerTileInputNeuralNetworkEncoder implements NeuralNetworkEncoder<GameState> {
    @Override
    public float[] encode(final GameState input) {
        float[] inputs = new float[GameState.BOARD_LENGTH];

        for (int x = 0; x < GameState.BOARD_LENGTH; x++) {
            inputs[x] = (float) input.getExponentialValue(x);
        }

        return inputs;
    }
}
