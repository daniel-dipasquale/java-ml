package com.dipasquale.ai.rl.neat.speciation.organism;

import com.dipasquale.ai.rl.neat.context.Context;
import com.dipasquale.ai.rl.neat.phenotype.NeuralNetwork;
import com.dipasquale.common.SerializableInteroperableStateMap;
import lombok.Getter;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public final class DefaultOrganismActivator implements OrganismActivator {
    @Getter
    private int complexity = 0;
    @Getter
    private float fitness = 0f;
    private NeuralNetwork neuralNetwork = null;

    @Override
    public void initialize(final Organism organism, final Context.NeuralNetworkSupport neuralNetworkSupport) {
        complexity = organism.getComplexity();
        fitness = organism.getFitness();
        neuralNetwork = organism.getPhenotype(neuralNetworkSupport);
    }

    @Override
    public float[] activate(final float[] inputs) {
        return neuralNetwork.activate(inputs);
    }

    @Override
    public void save(final ObjectOutputStream outputStream)
            throws IOException {
        SerializableInteroperableStateMap state = new SerializableInteroperableStateMap();

        state.put("organismActivator.complexity", complexity);
        state.put("organismActivator.fitness", fitness);
        state.put("organismActivator.neuralNetwork", neuralNetwork);
        state.writeTo(outputStream);
    }

    @Override
    public void load(final ObjectInputStream inputStream)
            throws IOException, ClassNotFoundException {
        SerializableInteroperableStateMap state = new SerializableInteroperableStateMap();

        state.readFrom(inputStream);
        complexity = state.get("organismActivator.complexity");
        fitness = state.get("organismActivator.fitness");
        neuralNetwork = state.get("organismActivator.neuralNetwork");
    }
}
