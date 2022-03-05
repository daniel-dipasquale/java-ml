package com.dipasquale.ai.rl.neat.phenotype;

import com.dipasquale.ai.rl.neat.genotype.Genome;

@FunctionalInterface
public interface NeatNeuralNetworkFactory {
    NeatNeuralNetwork create(Genome genome);
}
