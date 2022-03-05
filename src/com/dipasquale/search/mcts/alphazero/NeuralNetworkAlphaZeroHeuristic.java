package com.dipasquale.search.mcts.alphazero;

import com.dipasquale.ai.common.NeuralNetwork;
import com.dipasquale.ai.common.NeuralNetworkDecoder;
import com.dipasquale.ai.common.NeuralNetworkEncoder;
import com.dipasquale.ai.common.NeuronMemory;
import com.dipasquale.search.mcts.Action;
import com.dipasquale.search.mcts.EdgeFactory;
import com.dipasquale.search.mcts.SearchNode;
import com.dipasquale.search.mcts.State;

public final class NeuralNetworkAlphaZeroHeuristic<TAction extends Action, TState extends State<TAction, TState>, TNeuronMemory extends NeuronMemory> implements AlphaZeroHeuristic<TAction, TState> {
    private final NeuralNetworkEncoder<TState> encoder;
    private final NeuralNetworkDecoder<AlphaZeroPrediction<TAction, TState>, NeuralNetworkAlphaZeroHeuristicContext<TAction, TState>> decoder;
    private final NeuralNetwork<TNeuronMemory> neuralNetwork;
    private final TNeuronMemory neuronMemory;
    private final AlphaZeroValueCalculator<TAction, TState> valueCalculator;
    private final AlphaZeroPolicyDistributor<TAction, TState> policyDistributor;

    public NeuralNetworkAlphaZeroHeuristic(final NeuralNetworkEncoder<TState> encoder, final NeuralNetworkDecoder<AlphaZeroPrediction<TAction, TState>, NeuralNetworkAlphaZeroHeuristicContext<TAction, TState>> decoder, final NeuralNetwork<TNeuronMemory> neuralNetwork, final AlphaZeroValueCalculator<TAction, TState> valueCalculator, final AlphaZeroPolicyDistributor<TAction, TState> policyDistributor) {
        this.encoder = encoder;
        this.decoder = decoder;
        this.neuralNetwork = neuralNetwork;
        this.neuronMemory = neuralNetwork.createMemory();
        this.valueCalculator = valueCalculator;
        this.policyDistributor = policyDistributor;
    }

    @Override
    public boolean isEveryOutcomeDeterministic() {
        return policyDistributor == null;
    }

    @Override
    public AlphaZeroPrediction<TAction, TState> predict(final SearchNode<TAction, AlphaZeroEdge, TState> node, final EdgeFactory<AlphaZeroEdge> edgeFactory) {
        float[] input = encoder.encode(node.getState());
        float[] output = neuralNetwork.activate(input, neuronMemory);
        NeuralNetworkAlphaZeroHeuristicContext<TAction, TState> context = new NeuralNetworkAlphaZeroHeuristicContext<>(node, edgeFactory, valueCalculator, policyDistributor);

        return decoder.decode(context, output);
    }
}
