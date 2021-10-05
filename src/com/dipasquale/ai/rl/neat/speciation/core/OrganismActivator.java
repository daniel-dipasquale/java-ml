package com.dipasquale.ai.rl.neat.speciation.core;

import com.dipasquale.ai.rl.neat.genotype.Genome;
import com.dipasquale.ai.rl.neat.phenotype.GenomeActivator;
import com.dipasquale.ai.rl.neat.phenotype.NeuralNetwork;
import com.dipasquale.ai.rl.neat.phenotype.NeuronMemory;
import com.dipasquale.ai.rl.neat.speciation.organism.Organism;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public final class OrganismActivator implements NeuralNetwork, Serializable {
    @Serial
    private static final long serialVersionUID = -1443031688282143075L;
    private Organism organism = null;
    private GenomeActivator genomeActivator = null;

    void initialize(final Organism organism, final GenomeActivator genomeActivator) {
        this.organism = organism;
        this.genomeActivator = genomeActivator;
    }

    public Genome getGenome() {
        return genomeActivator.getGenome();
    }

    public float getFitness() {
        return organism.getFitness();
    }

    @Override
    public NeuronMemory createMemory() {
        return genomeActivator.createMemory();
    }

    @Override
    public float[] activate(final float[] input, final NeuronMemory neuronMemory) {
        return genomeActivator.activate(input, neuronMemory);
    }

    void reset() {
        organism = null;
        genomeActivator = null;
    }
}
