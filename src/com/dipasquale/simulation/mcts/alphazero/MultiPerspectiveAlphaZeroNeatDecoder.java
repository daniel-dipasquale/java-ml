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
public final class MultiPerspectiveAlphaZeroNeatDecoder<TAction extends Action, TEnvironment extends State<TAction, TEnvironment>> implements NeatDecoder<AlphaZeroPrediction, NeatAlphaZeroHeuristicContext<TAction, TEnvironment>> {
    private final int perspectiveParticipantId;
    private final int valueIndex;

    @Override
    public AlphaZeroPrediction decode(final NeatAlphaZeroHeuristicContext<TAction, TEnvironment> context, final float[] output) {
        return new MultiPerspectiveAlphaZeroPrediction<>(perspectiveParticipantId, valueIndex, context, output);
    }
}
