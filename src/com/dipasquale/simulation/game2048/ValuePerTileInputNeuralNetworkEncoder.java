package com.dipasquale.simulation.game2048;

import com.dipasquale.ai.common.NeuralNetworkEncoder;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class ValuePerTileInputNeuralNetworkEncoder implements NeuralNetworkEncoder<GameState> {
    @Override
    public float[] encode(final GameState input) {
        float[] results = new float[Board.LENGTH];

        for (int x = 0; x < Board.LENGTH; x++) {
            results[x] = (float) input.getValueFromTile(x);
        }

        return results;
    }
}
