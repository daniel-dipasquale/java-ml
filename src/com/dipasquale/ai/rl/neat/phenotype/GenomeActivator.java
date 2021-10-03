package com.dipasquale.ai.rl.neat.phenotype;

import com.dipasquale.ai.rl.neat.genotype.Genome;
import com.dipasquale.ai.rl.neat.genotype.NodeGeneType;
import com.dipasquale.ai.rl.neat.speciation.core.PopulationState;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@RequiredArgsConstructor
public final class GenomeActivator implements Serializable {
    @Serial
    private static final long serialVersionUID = 4096041177887342363L;
    private final Genome genome;
    private final PopulationState populationState;
    private final NeuralNetwork neuralNetwork;

    public String getId() {
        return genome.getId();
    }

    public int getGeneration() {
        return populationState.getGeneration();
    }

    public int getHiddenNodes() {
        return genome.getNodes().size(NodeGeneType.HIDDEN);
    }

    public int getConnections() {
        return genome.getConnections().getExpressed().size();
    }

    public float[] activate(final float[] input) {
        return neuralNetwork.activate(input);
    }

    boolean isOwnedBy(final Genome candidate) {
        return genome == candidate;
    }
}
