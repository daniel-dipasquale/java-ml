package com.dipasquale.ai.rl.neat.context;

import com.dipasquale.common.SerializableInteroperableStateMap;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

@RequiredArgsConstructor
public final class DefaultContext implements Context {
    private final DefaultContextGeneralSupport general;
    private final DefaultContextNodeGeneSupport nodes;
    private final DefaultContextConnectionGeneSupport connections;
    private final DefaultContextNeuralNetworkSupport neuralNetwork;
    private final DefaultContextParallelismSupport parallelism;
    private final DefaultContextRandomSupport random;
    private final DefaultContextMutationSupport mutation;
    private final DefaultContextCrossOverSupport crossOver;
    private final DefaultContextSpeciationSupport speciation;

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
    public void load(final ObjectInputStream inputStream, final StateOverrideSupport override)
            throws IOException, ClassNotFoundException {
        SerializableInteroperableStateMap state = new SerializableInteroperableStateMap();

        state.readFrom(inputStream);
        general.load(state);
        nodes.load(state, override.eventLoop());
        connections.load(state, override.eventLoop());
        neuralNetwork.load(state, override.eventLoop(), override.fitnessFunction());
        parallelism.load(state, override.eventLoop());
        random.load(state, override.eventLoop());
        mutation.load(state, override.eventLoop());
        crossOver.load(state, override.eventLoop());
        speciation.load(state, override.eventLoop());
    }
}
