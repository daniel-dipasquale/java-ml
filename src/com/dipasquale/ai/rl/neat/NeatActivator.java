package com.dipasquale.ai.rl.neat;

import com.dipasquale.ai.rl.neat.phenotype.NeatNeuralNetwork;

public interface NeatActivator extends NeatNeuralNetwork {
    NeatState getState();
}
