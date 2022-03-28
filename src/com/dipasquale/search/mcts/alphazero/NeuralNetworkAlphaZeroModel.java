package com.dipasquale.search.mcts.alphazero;

import com.dipasquale.ai.common.NeuralNetwork;
import com.dipasquale.ai.common.NeuralNetworkDecoder;
import com.dipasquale.ai.common.NeuralNetworkEncoder;
import com.dipasquale.ai.common.NeuronMemory;
import com.dipasquale.search.mcts.Action;
import com.dipasquale.search.mcts.EdgeFactory;
import com.dipasquale.search.mcts.SearchNode;
import com.dipasquale.search.mcts.State;
import com.dipasquale.search.mcts.common.ExplorationProbabilityCalculator;
import com.dipasquale.search.mcts.common.ValueHeuristic;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

public final class NeuralNetworkAlphaZeroModel<TAction extends Action, TState extends State<TAction, TState>, TNeuronMemory extends NeuronMemory> implements AlphaZeroModel<TAction, TState> {
    private final NeuralNetworkEncoder<TState> encoder;
    private final NeuralNetworkDecoder<AlphaZeroPrediction<TAction, TState>, NeuralNetworkAlphaZeroContext<TAction, TState>> decoder;
    private final NeuralNetwork<TNeuronMemory> neuralNetwork;
    private TNeuronMemory neuronMemory;
    private final ValueHeuristic<TAction, TState> valueHeuristic;
    private final ExplorationProbabilityCalculator<TAction> policyCalculator;

    public NeuralNetworkAlphaZeroModel(final NeuralNetworkEncoder<TState> encoder, final NeuralNetworkDecoder<AlphaZeroPrediction<TAction, TState>, NeuralNetworkAlphaZeroContext<TAction, TState>> decoder, final NeuralNetwork<TNeuronMemory> neuralNetwork, final ValueHeuristic<TAction, TState> valueHeuristic, final ExplorationProbabilityCalculator<TAction> policyCalculator) {
        this.encoder = encoder;
        this.decoder = decoder;
        this.neuralNetwork = neuralNetwork;
        this.neuronMemory = neuralNetwork.createMemory();
        this.valueHeuristic = valueHeuristic;
        this.policyCalculator = policyCalculator;
    }

    @Override
    public boolean isEveryStateIntentional() {
        return policyCalculator == null;
    }

    @Override
    public AlphaZeroPrediction<TAction, TState> predict(final SearchNode<TAction, AlphaZeroEdge, TState> searchNode, final EdgeFactory<AlphaZeroEdge> edgeFactory) {
        Predictor<TAction, TState, TNeuronMemory> predictor = new Predictor<>(encoder, neuralNetwork, neuronMemory);
        NeuralNetworkAlphaZeroContext<TAction, TState> context = new NeuralNetworkAlphaZeroContext<>(searchNode, edgeFactory, predictor, valueHeuristic, policyCalculator);

        return decoder.decode(context);
    }

    @Override
    public void reset() {
        neuronMemory = neuralNetwork.createMemory();
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class Predictor<TAction extends Action, TState extends State<TAction, TState>, TNeuronMemory extends NeuronMemory> implements AlphaZeroPredictor<TAction, TState> {
        private final NeuralNetworkEncoder<TState> encoder;
        private final NeuralNetwork<TNeuronMemory> neuralNetwork;
        private final TNeuronMemory neuronMemory;

        @Override
        public float[] predict(final SearchNode<TAction, AlphaZeroEdge, TState> searchNode) {
            float[] input = encoder.encode(searchNode.getState());

            return neuralNetwork.activate(input, neuronMemory);
        }
    }
}
