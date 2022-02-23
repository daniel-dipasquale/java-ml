package com.dipasquale.simulation.mcts.alphazero;

import com.dipasquale.ai.rl.neat.core.NeatDecoder;
import com.dipasquale.search.mcts.alphazero.AlphaZeroPrediction;
import com.dipasquale.search.mcts.core.Action;
import com.dipasquale.search.mcts.core.State;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public final class MultiPerspectiveAlphaZeroNeatDecoder<TAction extends Action, TState extends State<TAction, TState>> implements NeatDecoder<AlphaZeroPrediction<TAction, TState>, NeatAlphaZeroHeuristicContext<TAction, TState>> {
    private final int perspectiveParticipantId;
    private final boolean inverseOutputForOpponent;
    private final int valueIndex;

    @Override
    public AlphaZeroPrediction<TAction, TState> decode(final NeatAlphaZeroHeuristicContext<TAction, TState> context, final float[] output) {
        return new MultiPerspectiveAlphaZeroPrediction<>(perspectiveParticipantId, inverseOutputForOpponent, valueIndex, context, output);
    }
}
