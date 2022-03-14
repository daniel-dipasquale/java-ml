package com.dipasquale.ai.rl.neat.common;

import com.dipasquale.ai.rl.neat.phenotype.NeatNeuralNetwork;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class TwoPlayerGameFromOneSupport<T> implements TwoPlayerGameSupport<T> {
    private final OnePlayerGameSupport<T> support;

    @Override
    public T createPlayer(final NeatNeuralNetwork neuralNetwork) {
        return support.createPlayer(neuralNetwork);
    }

    @Override
    public T createClassicPlayer() {
        return null;
    }

    @Override
    public int play(final T player1, final T player2) {
        if (player1 != null) {
            return support.play(player1) ? 0 : 1;
        }

        return support.play(player2) ? 1 : 0;
    }
}
