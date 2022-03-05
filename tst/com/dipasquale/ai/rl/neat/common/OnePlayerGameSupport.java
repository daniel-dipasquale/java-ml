package com.dipasquale.ai.rl.neat.common;

import com.dipasquale.ai.rl.neat.phenotype.NeatNeuralNetwork;

public interface OnePlayerGameSupport<T> {
    T createPlayer(NeatNeuralNetwork neuralNetwork);

    boolean play(T player);

    default TwoPlayerGameSupport<T> createAsTwo() {
        return new TwoPlayerGameFromOneSupport<>(this);
    }
}
