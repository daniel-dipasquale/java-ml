package com.dipasquale.ai.rl.neat.common;

import com.dipasquale.ai.rl.neat.phenotype.NeatNeuralNetwork;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class TwoPlayerGameFromOneSupport<T> implements TwoPlayerGameSupport<T> {
    private final OnePlayerGameSupport<T> gameSupport;

    @Override
    public T createPlayer(final NeatNeuralNetwork neuralNetwork) {
        return gameSupport.createPlayer(neuralNetwork);
    }

    @Override
    public T createClassicPlayer() {
        return null;
    }

    @Override
    public int play(final T player1, final T player2) {
        if (player1 != null) {
            return gameSupport.play(player1) ? 0 : 1;
        }

        return gameSupport.play(player2) ? 1 : 0;
    }
}
