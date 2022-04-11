package com.dipasquale.search.mcts.alphazero;

import com.dipasquale.ai.common.NeuralNetworkDecoder;
import com.dipasquale.search.mcts.Action;
import com.dipasquale.search.mcts.SearchNode;
import com.dipasquale.search.mcts.State;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;

import java.util.EnumSet;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public final class AlphaZeroNeuralNetworkDecoder<TAction extends Action, TState extends State<TAction, TState>, TSearchNode extends SearchNode<TAction, AlphaZeroEdge, TState, TSearchNode>> implements NeuralNetworkDecoder<AlphaZeroPrediction<TAction, TState, TSearchNode>, NeuralNetworkAlphaZeroContext<TAction, TState, TSearchNode>> {
    private final int perspectiveParticipantId;
    private final EnumSet<PredictionBehaviorType> behaviorTypes;
    private final int valueIndex;

    @Override
    public AlphaZeroPrediction<TAction, TState, TSearchNode> decode(final NeuralNetworkAlphaZeroContext<TAction, TState, TSearchNode> context) {
        return new NeuralNetworkAlphaZeroPrediction<>(perspectiveParticipantId, behaviorTypes, valueIndex, context);
    }
}
