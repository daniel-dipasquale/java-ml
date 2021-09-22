package com.dipasquale.ai.rl.neat.speciation.organism;

import com.dipasquale.ai.rl.neat.context.Context;
import com.dipasquale.ai.rl.neat.genotype.GenomeActivator;
import com.dipasquale.common.serialization.SerializableStateGroup;
import lombok.Getter;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public final class DefaultOrganismActivator implements OrganismActivator {
    @Getter
    private int complexity = 0;
    @Getter
    private float fitness = 0f;
    private GenomeActivator genomeActivator = null;

    @Override
    public void initialize(final Organism organism, final Context.NeuralNetworkSupport neuralNetworkSupport) {
        complexity = organism.getComplexity();
        fitness = organism.getFitness();
        genomeActivator = organism.getGenomeActivator(neuralNetworkSupport);
    }

    @Override
    public float[] activate(final float[] inputs) {
        return genomeActivator.activate(inputs);
    }

    @Override
    public void save(final ObjectOutputStream outputStream)
            throws IOException {
        SerializableStateGroup state = new SerializableStateGroup();

        state.put("organismActivator.complexity", complexity);
        state.put("organismActivator.fitness", fitness);
        state.put("organismActivator.genomeActivator", genomeActivator);
        state.writeTo(outputStream);
    }

    @Override
    public void load(final ObjectInputStream inputStream)
            throws IOException, ClassNotFoundException {
        SerializableStateGroup state = new SerializableStateGroup();

        state.readFrom(inputStream);
        complexity = state.get("organismActivator.complexity");
        fitness = state.get("organismActivator.fitness");
        genomeActivator = state.get("organismActivator.genomeActivator");
    }
}
