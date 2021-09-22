package com.dipasquale.ai.rl.neat.phenotype;

import com.dipasquale.ai.rl.neat.genotype.Genome;

@FunctionalInterface
public interface NeuralNetworkFactory {
    NeuralNetwork create(Genome genome);
}
