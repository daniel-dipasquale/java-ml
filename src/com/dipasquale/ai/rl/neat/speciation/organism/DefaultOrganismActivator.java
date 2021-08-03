package com.dipasquale.ai.rl.neat.speciation.organism;

import com.dipasquale.ai.rl.neat.context.Context;
import com.dipasquale.data.structure.map.SerializableInteroperableStateMap;
import lombok.Getter;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public final class DefaultOrganismActivator implements OrganismActivator {
    private Organism organism = null;
    @Getter
    private float fitness = 0f;

    @Override
    public void setOrganism(final Organism newOrganism) {
        organism = newOrganism;
        fitness = newOrganism.getFitness();
    }

    @Override
    public float[] activate(final Context context, final float[] inputs) {
        return organism.activate(inputs);
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
