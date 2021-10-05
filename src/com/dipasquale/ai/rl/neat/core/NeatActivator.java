package com.dipasquale.ai.rl.neat.core;

import com.dipasquale.ai.rl.neat.phenotype.NeuralNetwork;

public interface NeatActivator extends NeuralNetwork {
    NeatState getState();
}
