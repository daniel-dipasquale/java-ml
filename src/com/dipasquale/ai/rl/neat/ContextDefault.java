package com.dipasquale.ai.rl.neat;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class ContextDefault<T extends Comparable<T>> implements Context<T> {
    private final ContextDefaultGeneralSupport<T> general;
    private final ContextDefaultNodeGeneSupport<T> nodes;
    private final ContextDefaultConnectionGeneSupport<T> connections;
    private final ContextDefaultNeuralNetworkSupport<T> neuralNetwork;
    private final ContextDefaultRandom random;
    private final ContextDefaultMutation mutation;
    private final ContextDefaultCrossOver<T> crossOver;
    private final ContextDefaultSpeciation<T> speciation;

    ContextDefault(final Factory<T, ContextDefaultGeneralSupport<T>> generalFactory,
                   final Factory<T, ContextDefaultNodeGeneSupport<T>> nodesFactory,
                   final Factory<T, ContextDefaultConnectionGeneSupport<T>> connectionsFactory,
                   final Factory<T, ContextDefaultNeuralNetworkSupport<T>> neuralNetworkFactory,
                   final Factory<T, ContextDefaultRandom> randomFactory,
                   final Factory<T, ContextDefaultMutation> mutationFactory,
                   final Factory<T, ContextDefaultCrossOver<T>> crossOverFactory,
                   final Factory<T, ContextDefaultSpeciation<T>> speciationFactory) {
        this.general = generalFactory.create(this);
        this.nodes = nodesFactory.create(this);
        this.connections = connectionsFactory.create(this);
        this.neuralNetwork = neuralNetworkFactory.create(this);
        this.random = randomFactory.create(this);
        this.mutation = mutationFactory.create(this);
        this.crossOver = crossOverFactory.create(this);
        this.speciation = speciationFactory.create(this);
    }

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
    public NeuralNetworkSupport<T> neuralNetwork() {
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
    public CrossOver<T> crossOver() {
        return crossOver;
    }

    @Override
    public Speciation<T> speciation() {
        return speciation;
    }

    @FunctionalInterface
    interface Factory<T extends Comparable<T>, R> {
        R create(ContextDefault<T> context);
    }
}
