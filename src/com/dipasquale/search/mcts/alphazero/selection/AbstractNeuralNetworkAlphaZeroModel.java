package com.dipasquale.search.mcts.alphazero.selection;

import com.dipasquale.ai.common.NeuralNetwork;
import com.dipasquale.ai.common.NeuralNetworkDecoder;
import com.dipasquale.ai.common.NeuronMemory;
import com.dipasquale.search.mcts.EdgeFactory;
import com.dipasquale.search.mcts.SearchNode;
import com.dipasquale.search.mcts.SearchNodeGroupProvider;
import com.dipasquale.search.mcts.State;
import com.dipasquale.search.mcts.alphazero.AlphaZeroEdge;
import com.dipasquale.search.mcts.heuristic.intention.ExplorationHeuristic;
import com.dipasquale.search.mcts.heuristic.selection.RewardHeuristic;

public abstract class AbstractNeuralNetworkAlphaZeroModel<TAction, TState extends State<TAction, TState>, TSearchNode extends SearchNode<TAction, AlphaZeroEdge, TState, TSearchNode>, TNeuronMemory extends NeuronMemory> implements AlphaZeroModel<TAction, TState, TSearchNode> {
    private final NeuralNetworkDecoder<AlphaZeroPrediction<TAction, TState, TSearchNode>, NeuralNetworkAlphaZeroContext<TAction, TState, TSearchNode>> decoder;
    private final NeuralNetwork<TNeuronMemory> neuralNetwork;
    private TNeuronMemory neuronMemory;
    private final RewardHeuristic<TAction, TState> rewardHeuristic;
    private final ExplorationHeuristic<TAction> explorationHeuristic;

    protected AbstractNeuralNetworkAlphaZeroModel(final NeuralNetworkDecoder<AlphaZeroPrediction<TAction, TState, TSearchNode>, NeuralNetworkAlphaZeroContext<TAction, TState, TSearchNode>> decoder, final NeuralNetwork<TNeuronMemory> neuralNetwork, final RewardHeuristic<TAction, TState> rewardHeuristic, final ExplorationHeuristic<TAction> explorationHeuristic) {
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

    protected abstract AlphaZeroPredictor<TAction, TState, TSearchNode> createPredictor(TNeuronMemory neuronMemory);

    @Override
    public AlphaZeroPrediction<TAction, TState, TSearchNode> predict(final TSearchNode searchNode, final EdgeFactory<AlphaZeroEdge> edgeFactory, final SearchNodeGroupProvider<TAction, AlphaZeroEdge, TState, TSearchNode> searchNodeGroupProvider) {
        AlphaZeroPredictor<TAction, TState, TSearchNode> predictor = createPredictor(neuronMemory);
        NeuralNetworkAlphaZeroContext<TAction, TState, TSearchNode> context = new NeuralNetworkAlphaZeroContext<>(searchNode, edgeFactory, searchNodeGroupProvider, predictor, rewardHeuristic, explorationHeuristic);

        return decoder.decode(context);
    }

    @Override
    public void reset() {
        neuronMemory = neuralNetwork.createMemory();
    }
}
