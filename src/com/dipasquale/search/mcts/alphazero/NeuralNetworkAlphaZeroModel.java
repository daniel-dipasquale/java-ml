package com.dipasquale.search.mcts.alphazero;

import com.dipasquale.ai.common.NeuralNetwork;
import com.dipasquale.ai.common.NeuralNetworkDecoder;
import com.dipasquale.ai.common.NeuralNetworkEncoder;
import com.dipasquale.ai.common.NeuronMemory;
import com.dipasquale.search.mcts.Action;
import com.dipasquale.search.mcts.EdgeFactory;
import com.dipasquale.search.mcts.SearchNode;
import com.dipasquale.search.mcts.State;
import com.dipasquale.search.mcts.common.ExplorationHeuristic;
import com.dipasquale.search.mcts.common.RewardHeuristic;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

public final class NeuralNetworkAlphaZeroModel<TAction extends Action, TState extends State<TAction, TState>, TNeuronMemory extends NeuronMemory> implements AlphaZeroModel<TAction, TState> {
    private final NeuralNetworkEncoder<TState> encoder;
    private final NeuralNetworkDecoder<AlphaZeroPrediction<TAction, TState>, NeuralNetworkAlphaZeroContext<TAction, TState>> decoder;
    private final NeuralNetwork<TNeuronMemory> neuralNetwork;
    private TNeuronMemory neuronMemory;
    private final RewardHeuristic<TAction, TState> rewardHeuristic;
    private final ExplorationHeuristic<TAction> explorationHeuristic;

    public NeuralNetworkAlphaZeroModel(final NeuralNetworkEncoder<TState> encoder, final NeuralNetworkDecoder<AlphaZeroPrediction<TAction, TState>, NeuralNetworkAlphaZeroContext<TAction, TState>> decoder, final NeuralNetwork<TNeuronMemory> neuralNetwork, final RewardHeuristic<TAction, TState> rewardHeuristic, final ExplorationHeuristic<TAction> explorationHeuristic) {
        this.encoder = encoder;
        this.decoder = decoder;
        this.neuralNetwork = neuralNetwork;
        this.neuronMemory = neuralNetwork.createMemory();
        this.rewardHeuristic = rewardHeuristic;
        this.explorationHeuristic = explorationHeuristic;
    }

    @Override
    public boolean isEveryStateIntentional() {
        return explorationHeuristic == null;
    }

    @Override
    public AlphaZeroPrediction<TAction, TState> predict(final SearchNode<TAction, AlphaZeroEdge, TState> searchNode, final EdgeFactory<AlphaZeroEdge> edgeFactory) {
        Predictor<TAction, TState, TNeuronMemory> predictor = new Predictor<>(encoder, neuralNetwork, neuronMemory);
        NeuralNetworkAlphaZeroContext<TAction, TState> context = new NeuralNetworkAlphaZeroContext<>(searchNode, edgeFactory, predictor, rewardHeuristic, explorationHeuristic);

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
