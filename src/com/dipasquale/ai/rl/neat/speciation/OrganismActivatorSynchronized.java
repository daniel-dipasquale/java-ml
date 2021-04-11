package com.dipasquale.ai.rl.neat.speciation;

import com.dipasquale.ai.rl.neat.context.Context;
import com.dipasquale.data.structure.map.SerializableInteroperableStateMap;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Deque;
import java.util.LinkedList;

public final class OrganismActivatorSynchronized implements OrganismActivator {
    private Organism organism = null;
    private float fitness = 0f;
    private final Deque<Organism> clonedOrganisms = new LinkedList<>();

    @Override
    public void setOrganism(final Organism newOrganism) {
        synchronized (clonedOrganisms) {
            organism = newOrganism;
            fitness = newOrganism.getFitness();
            clonedOrganisms.clear();
        }
    }

    @Override
    public float getFitness() {
        synchronized (clonedOrganisms) {
            return fitness;
        }
    }

    private Organism getOrCloneOrganism(final Context context) {
        synchronized (clonedOrganisms) {
            if (clonedOrganisms.isEmpty()) {
                return organism.createClone(context);
            }

            return clonedOrganisms.removeFirst();
        }
    }

    private void enqueueOrganism(final Organism organism) {
        synchronized (clonedOrganisms) {
            clonedOrganisms.add(organism);
        }
    }

    @Override
    public float[] activate(final Context context, final float[] inputs) {
        Organism organism = getOrCloneOrganism(context);

        try {
            return organism.activate(inputs);
        } finally {
            enqueueOrganism(organism);
        }
    }

    @Override
    public void save(final ObjectOutputStream outputStream)
            throws IOException {
        SerializableInteroperableStateMap state = new SerializableInteroperableStateMap();

        state.put("organismActivator.organism", organism);
        state.put("organismActivator.fitness", fitness);
        state.writeTo(outputStream);
    }

    @Override
    public void load(final ObjectInputStream inputStream)
            throws IOException, ClassNotFoundException {
        SerializableInteroperableStateMap state = new SerializableInteroperableStateMap();

        state.readFrom(inputStream);
        organism = state.get("organismActivator.organism");
        fitness = state.get("organismActivator.fitness");
    }
}
