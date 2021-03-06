package com.dipasquale.ai.rl.neat;

final class ContextDefault<T extends Comparable<T>> implements Context<T> {
    private final ContextDefaultGeneralSupport<T> general;
    private final ContextDefaultNodeGeneSupport<T> nodes;
    private final ContextDefaultConnectionGeneSupport<T> connections;
    private final ContextDefaultNeuralNetworkSupport<T> neuralNetwork;
    private final ContextDefaultRandom random;
    private final ContextDefaultMutation mutation;
    private final ContextDefaultCrossOver<T> crossOver;
    private final ContextDefaultSpeciation<T> speciation;

    ContextDefault(final ContextDefaultComponentFactory<T, ContextDefaultGeneralSupport<T>> generalFactory,
                   final ContextDefaultComponentFactory<T, ContextDefaultNodeGeneSupport<T>> nodesFactory,
                   final ContextDefaultComponentFactory<T, ContextDefaultConnectionGeneSupport<T>> connectionsFactory,
                   final ContextDefaultComponentFactory<T, ContextDefaultNeuralNetworkSupport<T>> neuralNetworkFactory,
                   final ContextDefaultComponentFactory<T, ContextDefaultRandom> randomFactory,
                   final ContextDefaultComponentFactory<T, ContextDefaultMutation> mutationFactory,
                   final ContextDefaultComponentFactory<T, ContextDefaultCrossOver<T>> crossOverFactory,
                   final ContextDefaultComponentFactory<T, ContextDefaultSpeciation<T>> speciationFactory) {
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
}
