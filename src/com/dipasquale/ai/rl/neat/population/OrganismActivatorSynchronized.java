package com.dipasquale.ai.rl.neat.population;

import com.dipasquale.ai.rl.neat.context.Context;
import com.dipasquale.ai.rl.neat.genotype.Organism;

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
}
