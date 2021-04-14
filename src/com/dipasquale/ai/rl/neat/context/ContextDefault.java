package com.dipasquale.ai.rl.neat.context;

import com.dipasquale.data.structure.map.SerializableInteroperableStateMap;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

@RequiredArgsConstructor
public final class ContextDefault implements Context {
    private final ContextDefaultGeneralSupport general;
    private final ContextDefaultNodeGeneSupport nodes;
    private final ContextDefaultConnectionGeneSupport connections;
    private final ContextDefaultNeuralNetworkSupport neuralNetwork;
    private final ContextDefaultParallelismSupport parallelism;
    private final ContextDefaultRandomSupport random;
    private final ContextDefaultMutationSupport mutation;
    private final ContextDefaultCrossOverSupport crossOver;
    private final ContextDefaultSpeciationSupport speciation;

    @Override
    public GeneralSupport general() {
        return general;
    }

    @Override
    public NodeGeneSupport nodes() {
        return nodes;
    }

    @Override
    public ConnectionGeneSupport connections() {
        return connections;
    }

    @Override
    public NeuralNetworkSupport neuralNetwork() {
        return neuralNetwork;
    }

    @Override
    public ParallelismSupport parallelism() {
        return parallelism;
    }

    @Override
    public RandomSupport random() {
        return random;
    }

    @Override
    public MutationSupport mutation() {
        return mutation;
    }

    @Override
    public CrossOverSupport crossOver() {
        return crossOver;
    }

    @Override
    public SpeciationSupport speciation() {
        return speciation;
    }

    @Override
    public void save(final ObjectOutputStream outputStream)
            throws IOException {
        SerializableInteroperableStateMap state = new SerializableInteroperableStateMap();

        general.save(state);
        nodes.save(state);
        connections.save(state);
        neuralNetwork.save(state);
        parallelism.save(state);
        random.save(state);
        mutation.save(state);
        crossOver.save(state);
        speciation.save(state);
        state.writeTo(outputStream);
    }

    @Override
    public void load(final ObjectInputStream inputStream, final Context.StateOverrideSupport override)
            throws IOException, ClassNotFoundException {
        SerializableInteroperableStateMap state = new SerializableInteroperableStateMap();

        state.readFrom(inputStream);
        general.load(state, override.environment());
        nodes.load(state, override.eventLoop());
        connections.load(state, override.eventLoop());
        neuralNetwork.load(state);
        parallelism.load(state, override.eventLoop());
        random.load(state);
        mutation.load(state);
        crossOver.load(state);
        speciation.load(state);
    }
}
