package com.dipasquale.ai.rl.neat.context;

import com.dipasquale.data.structure.map.SerializableInteroperableStateMap;
import lombok.RequiredArgsConstructor;

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
    private final ContextDefaultStateSupport state = new ContextDefaultStateSupport(this);

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
    public StateSupport state() {
        return state;
    }

    void save(final SerializableInteroperableStateMap state) {
        general.save(state);
        nodes.save(state);
        connections.save(state);
        neuralNetwork.save(state);
        parallelism.save(state);
        random.save(state);
        mutation.save(state);
        crossOver.save(state);
        speciation.save(state);
    }

    public void load(final SerializableInteroperableStateMap state, final Context.StateOverrideSupport override) {
        general.load(state, override.environment());
        nodes.load(state);
        connections.load(state);
        neuralNetwork.load(state);
        parallelism.load(state, override.eventLoop());
        random.load(state);
        mutation.load(state);
        crossOver.load(state);
        speciation.load(state);
    }
}
