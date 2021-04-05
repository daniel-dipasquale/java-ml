package com.dipasquale.ai.rl.neat.context;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class ContextDefault implements Context {
    private final ContextDefaultGeneralSupport general;
    private final ContextDefaultNodeGeneSupport nodes;
    private final ContextDefaultConnectionGeneSupport connections;
    private final ContextDefaultNeuralNetworkSupport neuralNetwork;
    private final ContextDefaultRandom random;
    private final ContextDefaultParallelism parallelism;
    private final ContextDefaultMutation mutation;
    private final ContextDefaultCrossOver crossOver;
    private final ContextDefaultSpeciation speciation;

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
    public Random random() {
        return random;
    }

    @Override
    public Parallelism parallelism() {
        return parallelism;
    }

    @Override
    public Mutation mutation() {
        return mutation;
    }

    @Override
    public CrossOver crossOver() {
        return crossOver;
    }

    @Override
    public Speciation speciation() {
        return speciation;
    }
}
