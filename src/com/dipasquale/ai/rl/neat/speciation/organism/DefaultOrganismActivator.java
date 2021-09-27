package com.dipasquale.ai.rl.neat.speciation.organism;

import com.dipasquale.ai.rl.neat.context.Context;
import com.dipasquale.ai.rl.neat.phenotype.GenomeActivator;
import com.dipasquale.common.serialization.SerializableStateGroup;
import lombok.Getter;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public final class DefaultOrganismActivator implements OrganismActivator {
    @Getter
    private int hiddenNodes = 0;
    @Getter
    private int connections = 0;
    @Getter
    private float fitness = 0f;
    private GenomeActivator genomeActivator = null;

    @Override
    public void initialize(final Organism organism, final Context.ActivationSupport activationSupport) {
        hiddenNodes = organism.getHiddenNodes();
        connections = organism.getConnections();
        fitness = organism.getFitness();
        genomeActivator = organism.getGenomeActivator(activationSupport);
    }

    @Override
    public float[] activate(final float[] inputs) {
        return genomeActivator.activate(inputs);
    }

    @Override
    public void save(final ObjectOutputStream outputStream)
            throws IOException {
        SerializableStateGroup state = new SerializableStateGroup();

        state.put("organismActivator.hiddenNodes", connections);
        state.put("organismActivator.connections", connections);
        state.put("organismActivator.fitness", fitness);
        state.put("organismActivator.genomeActivator", genomeActivator);
        state.writeTo(outputStream);
    }

    @Override
    public void load(final ObjectInputStream inputStream)
            throws IOException, ClassNotFoundException {
        SerializableStateGroup state = new SerializableStateGroup();

        state.readFrom(inputStream);
        hiddenNodes = state.get("organismActivator.hiddenNodes");
        connections = state.get("organismActivator.connections");
        fitness = state.get("organismActivator.fitness");
        genomeActivator = state.get("organismActivator.genomeActivator");
    }
}
