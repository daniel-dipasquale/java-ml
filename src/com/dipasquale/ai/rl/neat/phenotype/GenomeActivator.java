package com.dipasquale.ai.rl.neat.phenotype;

import com.dipasquale.ai.rl.neat.genotype.Genome;
import com.dipasquale.ai.rl.neat.speciation.core.PopulationState;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public final class GenomeActivator implements NeuralNetwork, Serializable {
    @Serial
    private static final long serialVersionUID = 4096041177887342363L;
    @Getter
    private final Genome genome;
    private final PopulationState populationState;
    private final NeuralNetwork neuralNetwork;

    public int getIteration() {
        return populationState.getIteration();
    }

    public int getGeneration() {
        return populationState.getGeneration();
    }

    @Override
    public NeuronMemory createMemory() {
        return neuralNetwork.createMemory();
    }

    @Override
    public float[] activate(final float[] input, final NeuronMemory neuronMemory) {
        return neuralNetwork.activate(input, neuronMemory);
    }

    boolean isOwnedBy(final Genome candidate) {
        return genome == candidate;
    }
}
