package com.dipasquale.ai.rl.neat.speciation.organism;

import com.dipasquale.ai.rl.neat.phenotype.GenomeActivator;
import com.dipasquale.common.serialization.SerializableStateGroup;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public final class OrganismActivator {
    private Organism organism;
    private GenomeActivator genomeActivator = null;

    public void initialize(final Organism organism, final GenomeActivator genomeActivator) {
        this.organism = organism;
        this.genomeActivator = genomeActivator;
    }

    public int getHiddenNodes() {
        return organism.getHiddenNodes();
    }

    public int getConnections() {
        return organism.getConnections();
    }

    public float getFitness() {
        return organism.getFitness();
    }

    public float[] activate(final float[] inputs) {
        return genomeActivator.activate(inputs);
    }

    public void save(final ObjectOutputStream outputStream)
            throws IOException {
        SerializableStateGroup stateGroup = new SerializableStateGroup();

        stateGroup.put("organismActivator.organism", organism);
        stateGroup.put("organismActivator.genomeActivator", genomeActivator);
        stateGroup.writeTo(outputStream);
    }

    public void load(final ObjectInputStream inputStream)
            throws IOException, ClassNotFoundException {
        SerializableStateGroup stateGroup = new SerializableStateGroup();

        stateGroup.readFrom(inputStream);
        organism = stateGroup.get("organismActivator.organism");
        genomeActivator = stateGroup.get("organismActivator.genomeActivator");
    }

    public void reset() {
        organism = null;
        genomeActivator = null;
    }
}
