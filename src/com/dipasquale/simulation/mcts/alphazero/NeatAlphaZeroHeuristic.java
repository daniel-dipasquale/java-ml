package com.dipasquale.simulation.mcts.alphazero;

import com.dipasquale.ai.rl.neat.core.NeatDecoder;
import com.dipasquale.ai.rl.neat.core.NeatEncoder;
import com.dipasquale.ai.rl.neat.phenotype.NeuralNetwork;
import com.dipasquale.ai.rl.neat.phenotype.NeuronMemory;
import com.dipasquale.search.mcts.alphazero.AlphaZeroEdge;
import com.dipasquale.search.mcts.alphazero.AlphaZeroHeuristic;
import com.dipasquale.search.mcts.alphazero.AlphaZeroPrediction;
import com.dipasquale.search.mcts.core.Action;
import com.dipasquale.search.mcts.core.SearchNode;
import com.dipasquale.search.mcts.core.State;

public final class NeatAlphaZeroHeuristic<TAction extends Action, TEnvironment extends State<TAction, TEnvironment>> implements AlphaZeroHeuristic<TAction, TEnvironment> {
    private final NeatEncoder<TEnvironment> encoder;
    private final NeatDecoder<AlphaZeroPrediction, NeatAlphaZeroHeuristicContext<TAction, TEnvironment>> decoder;
    private final NeuralNetwork neuralNetwork;
    private final NeuronMemory neuronMemory;

    public NeatAlphaZeroHeuristic(final NeatEncoder<TEnvironment> encoder, final NeatDecoder<AlphaZeroPrediction, NeatAlphaZeroHeuristicContext<TAction, TEnvironment>> decoder, final NeuralNetwork neuralNetwork) {
        this.encoder = encoder;
        this.decoder = decoder;
        this.neuralNetwork = neuralNetwork;
        this.neuronMemory = neuralNetwork.createMemory();
    }

    @Override
    public AlphaZeroPrediction predict(final SearchNode<TAction, AlphaZeroEdge, TEnvironment> node, final int childrenCount) {
        TEnvironment environment = node.getState();
        float[] input = encoder.encode(environment);
        float[] output = neuralNetwork.activate(input, neuronMemory);

        return decoder.decode(new NeatAlphaZeroHeuristicContext<>(environment, childrenCount), output);
    }
}
