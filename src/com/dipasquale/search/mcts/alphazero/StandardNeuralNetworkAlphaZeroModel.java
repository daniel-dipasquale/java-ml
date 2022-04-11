package com.dipasquale.search.mcts.alphazero;

import com.dipasquale.ai.common.NeuralNetwork;
import com.dipasquale.ai.common.NeuralNetworkDecoder;
import com.dipasquale.ai.common.NeuralNetworkEncoder;
import com.dipasquale.ai.common.NeuronMemory;
import com.dipasquale.search.mcts.Action;
import com.dipasquale.search.mcts.StandardSearchNode;
import com.dipasquale.search.mcts.State;
import com.dipasquale.search.mcts.common.ExplorationHeuristic;
import com.dipasquale.search.mcts.common.RewardHeuristic;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

public final class StandardNeuralNetworkAlphaZeroModel<TAction extends Action, TState extends State<TAction, TState>, TNeuronMemory extends NeuronMemory> extends AbstractNeuralNetworkAlphaZeroModel<TAction, TState, StandardSearchNode<TAction, AlphaZeroEdge, TState>, TNeuronMemory> {
    private final NeuralNetworkEncoder<TState> encoder;
    private final NeuralNetwork<TNeuronMemory> neuralNetwork;

    public StandardNeuralNetworkAlphaZeroModel(final NeuralNetworkEncoder<TState> encoder, final NeuralNetworkDecoder<AlphaZeroPrediction<TAction, TState, StandardSearchNode<TAction, AlphaZeroEdge, TState>>, NeuralNetworkAlphaZeroContext<TAction, TState, StandardSearchNode<TAction, AlphaZeroEdge, TState>>> decoder, final NeuralNetwork<TNeuronMemory> neuralNetwork, final RewardHeuristic<TAction, TState> rewardHeuristic, final ExplorationHeuristic<TAction> explorationHeuristic) {
        super(decoder, neuralNetwork, rewardHeuristic, explorationHeuristic);
        this.encoder = encoder;
        this.neuralNetwork = neuralNetwork;
    }

    @Override
    protected AlphaZeroPredictor<TAction, TState, StandardSearchNode<TAction, AlphaZeroEdge, TState>> createPredictor(final TNeuronMemory neuronMemory) {
        return new Predictor(encoder, neuralNetwork, neuronMemory);
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private final class Predictor implements AlphaZeroPredictor<TAction, TState, StandardSearchNode<TAction, AlphaZeroEdge, TState>> {
        private final NeuralNetworkEncoder<TState> encoder;
        private final NeuralNetwork<TNeuronMemory> neuralNetwork;
        private final TNeuronMemory neuronMemory;

        @Override
        public float[] predict(final StandardSearchNode<TAction, AlphaZeroEdge, TState> searchNode) {
            float[] input = encoder.encode(searchNode.getState());

            return neuralNetwork.activate(input, neuronMemory);
        }
    }
}
