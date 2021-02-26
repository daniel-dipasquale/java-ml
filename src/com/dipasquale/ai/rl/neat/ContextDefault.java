package com.dipasquale.ai.rl.neat;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class ContextDefault<T extends Comparable<T>> implements Context<T> {
    private final GeneralSupportDefault<T> general;
    private final NodeGeneSupportDefault<T> nodes;
    private final ConnectionGeneSupportDefault<T> connections;
    private final NeuralNetworkSupportDefault neuralNetwork;
    private final RandomDefault random;
    private final MutationDefault mutation;
    private final CrossOverDefault crossover;
    private final SpeciationDefault<T> speciation;

    @Override
    public GeneralSupport<T> general() {
        return general;
    }

    @Override
    public NodeGeneSupport<T> nodes() {
        return nodes;
    }

    @Override
    public ConnectionGeneSupport<T> connections() {
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
    public Mutation mutation() {
        return mutation;
    }

    @Override
    public CrossOver crossover() {
        return crossover;
    }

    @Override
    public Speciation<T> speciation() {
        return speciation;
    }
}
