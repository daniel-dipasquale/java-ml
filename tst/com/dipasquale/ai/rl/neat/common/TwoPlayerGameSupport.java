package com.dipasquale.ai.rl.neat.common;

import com.dipasquale.ai.rl.neat.phenotype.NeatNeuralNetwork;

public interface TwoPlayerGameSupport<T> {
    T createPlayer(NeatNeuralNetwork neuralNetwork);

    T createClassicPlayer();

    int play(T player1, T player2);
}
