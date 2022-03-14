package com.dipasquale.search.mcts.alphazero;

import com.dipasquale.ai.common.NeuralNetworkDecoder;
import com.dipasquale.search.mcts.Action;
import com.dipasquale.search.mcts.State;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;

import java.util.EnumSet;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public final class AlphaZeroNeuralNetworkDecoder<TAction extends Action, TState extends State<TAction, TState>> implements NeuralNetworkDecoder<AlphaZeroPrediction<TAction, TState>, NeuralNetworkAlphaZeroModelContext<TAction, TState>> {
    private final int perspectiveParticipantId;
    private final EnumSet<PredictionBehaviorType> behaviorType;
    private final int valueIndex;

    @Override
    public AlphaZeroPrediction<TAction, TState> decode(final NeuralNetworkAlphaZeroModelContext<TAction, TState> context, final float[] output) {
        return new NeuralNetworkAlphaZeroPrediction<>(perspectiveParticipantId, behaviorType, valueIndex, context, output);
    }
}
