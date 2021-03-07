package com.dipasquale.ai.rl.neat;

final class ContextDefault implements Context {
    private final ContextDefaultGeneralSupport general;
    private final ContextDefaultNodeGeneSupport nodes;
    private final ContextDefaultConnectionGeneSupport connections;
    private final ContextDefaultNeuralNetworkSupport neuralNetwork;
    private final ContextDefaultRandom random;
    private final ContextDefaultMutation mutation;
    private final ContextDefaultCrossOver crossOver;
    private final ContextDefaultSpeciation speciation;

    ContextDefault(final ContextDefaultComponentFactory<ContextDefaultGeneralSupport> generalFactory,
                   final ContextDefaultComponentFactory<ContextDefaultNodeGeneSupport> nodesFactory,
                   final ContextDefaultComponentFactory<ContextDefaultConnectionGeneSupport> connectionsFactory,
                   final ContextDefaultComponentFactory<ContextDefaultNeuralNetworkSupport> neuralNetworkFactory,
                   final ContextDefaultComponentFactory<ContextDefaultRandom> randomFactory,
                   final ContextDefaultComponentFactory<ContextDefaultMutation> mutationFactory,
                   final ContextDefaultComponentFactory<ContextDefaultCrossOver> crossOverFactory,
                   final ContextDefaultComponentFactory<ContextDefaultSpeciation> speciationFactory) {
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
