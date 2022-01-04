package com.dipasquale.simulation.mcts.alphazero;

import com.dipasquale.ai.rl.neat.core.NeatDecoder;
import com.dipasquale.search.mcts.alphazero.AlphaZeroPrediction;
import com.dipasquale.search.mcts.core.Environment;
import com.dipasquale.search.mcts.core.SearchState;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public final class MultiPerspectiveAlphaZeroNeatDecoder<TState extends SearchState, TEnvironment extends Environment<TState, TEnvironment>> implements NeatDecoder<AlphaZeroPrediction, NeatAlphaZeroHeuristicContext<TState, TEnvironment>> {
    private final int perspectiveParticipantId;
    private final int valueIndex;

    @Override
    public AlphaZeroPrediction decode(final NeatAlphaZeroHeuristicContext<TState, TEnvironment> context, final float[] output) {
        TEnvironment environment = context.getEnvironment();
        int nextParticipantId = environment.getNextParticipantId();
        int currentParticipantId = environment.getCurrentState().getParticipantId();

        if (context.getChildrenCount() <= output.length - 1) {
            return new MultiPerspectivePolicySuperSetAlphaZeroPrediction(nextParticipantId, currentParticipantId, perspectiveParticipantId, output, valueIndex);
        }

        return new MultiPerspectivePolicySubSetAlphaZeroPrediction(nextParticipantId, currentParticipantId, perspectiveParticipantId, output, valueIndex);
    }
}
