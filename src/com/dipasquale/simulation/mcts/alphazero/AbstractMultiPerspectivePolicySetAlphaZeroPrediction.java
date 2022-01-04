package com.dipasquale.simulation.mcts.alphazero;

import com.dipasquale.search.mcts.alphazero.AlphaZeroPrediction;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
abstract class AbstractMultiPerspectivePolicySetAlphaZeroPrediction implements AlphaZeroPrediction {
    private final int nextParticipantId;
    private final int currentParticipantId;
    private final int perspectiveParticipantId;
    private final float[] output;
    private final int valueIndex;

    protected abstract int getPolicyIndex(int index);

    @Override
    public final float getPolicy(final int index) {
        int indexFixed = getPolicyIndex(index);

        if (nextParticipantId == perspectiveParticipantId) {
            return output[indexFixed];
        }

        return 1f - output[indexFixed];
    }

    @Override
    public final float getValue() {
        if (currentParticipantId == -1) {
            return 0f;
        }

        if (currentParticipantId == perspectiveParticipantId) {
            return output[valueIndex];
        }

        return -output[valueIndex];
    }
}
