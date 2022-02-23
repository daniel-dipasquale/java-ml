package com.dipasquale.simulation.mcts.alphazero;

import com.dipasquale.ai.rl.neat.core.NeatDecoder;
import com.dipasquale.ai.rl.neat.core.NeatEncoder;
import com.dipasquale.ai.rl.neat.phenotype.NeuralNetwork;
import com.dipasquale.ai.rl.neat.phenotype.NeuronMemory;
import com.dipasquale.search.mcts.alphazero.AlphaZeroEdge;
import com.dipasquale.search.mcts.alphazero.AlphaZeroHeuristic;
import com.dipasquale.search.mcts.alphazero.AlphaZeroPrediction;
import com.dipasquale.search.mcts.core.Action;
import com.dipasquale.search.mcts.core.EdgeFactory;
import com.dipasquale.search.mcts.core.SearchNode;
import com.dipasquale.search.mcts.core.State;

public final class NeatAlphaZeroHeuristic<TAction extends Action, TState extends State<TAction, TState>> implements AlphaZeroHeuristic<TAction, TState> {
    private final NeatEncoder<TState> encoder;
    private final NeatDecoder<AlphaZeroPrediction<TAction, TState>, NeatAlphaZeroHeuristicContext<TAction, TState>> decoder;
    private final NeuralNetwork neuralNetwork;
    private final NeuronMemory neuronMemory;

    public NeatAlphaZeroHeuristic(final NeatEncoder<TState> encoder, final NeatDecoder<AlphaZeroPrediction<TAction, TState>, NeatAlphaZeroHeuristicContext<TAction, TState>> decoder, final NeuralNetwork neuralNetwork) {
        this.encoder = encoder;
        this.decoder = decoder;
        this.neuralNetwork = neuralNetwork;
        this.neuronMemory = neuralNetwork.createMemory();
    }

    @Override
    public AlphaZeroPrediction<TAction, TState> predict(final SearchNode<TAction, AlphaZeroEdge, TState> node, final EdgeFactory<AlphaZeroEdge> edgeFactory) {
        float[] input = encoder.encode(node.getState());
        float[] output = neuralNetwork.activate(input, neuronMemory);

        return decoder.decode(new NeatAlphaZeroHeuristicContext<>(node, edgeFactory), output);
    }
}
