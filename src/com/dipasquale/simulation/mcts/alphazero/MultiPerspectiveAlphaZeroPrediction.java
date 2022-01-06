package com.dipasquale.simulation.mcts.alphazero;

import com.dipasquale.search.mcts.alphazero.AlphaZeroPrediction;
import com.dipasquale.search.mcts.core.Environment;
import com.dipasquale.search.mcts.core.SearchState;

public final class MultiPerspectiveAlphaZeroPrediction<TState extends SearchState, TEnvironment extends Environment<TState, TEnvironment>> implements AlphaZeroPrediction {
    private static final float MAX_VALUE_LESS_THAN_ONE = Float.intBitsToFloat(Float.floatToRawIntBits(1f) - 1);
    private final boolean policySuperSet;
    private final int nextParticipantId;
    private final int currentParticipantId;
    private final int perspectiveParticipantId;
    private final int valueIndex;
    private final float[] output;

    MultiPerspectiveAlphaZeroPrediction(final int perspectiveParticipantId, final int valueIndex, final NeatAlphaZeroHeuristicContext<TState, TEnvironment> context, final float[] output) {
        TEnvironment environment = context.getEnvironment();

        this.policySuperSet = context.getChildrenCount() <= output.length - 1;
        this.nextParticipantId = environment.getNextParticipantId();
        this.currentParticipantId = environment.getCurrentState().getParticipantId();
        this.perspectiveParticipantId = perspectiveParticipantId;
        this.valueIndex = valueIndex;
        this.output = output;
    }

    private static int getIndex(final float index, final float max) {
        float total = max - MAX_VALUE_LESS_THAN_ONE;
        float value = index / total;

        return (int) Math.floor(value * max);
    }

    private int getPolicyIndex(final int index) {
        if (policySuperSet) {
            if (index < valueIndex) {
                return index;
            }

            return index + 1;
        }

        int indexFixed = getIndex((float) index, (float) (output.length - 1));

        if (indexFixed < valueIndex) {
            return indexFixed;
        }

        return indexFixed + 1;
    }

    @Override
    public float getPolicy(final int index) {
        int indexFixed = getPolicyIndex(index);

        if (nextParticipantId == perspectiveParticipantId) {
            return output[indexFixed];
        }

        return 1f - output[indexFixed];
    }

    @Override
    public float getValue() {
        if (currentParticipantId == -1) {
            return 0f;
        }

        if (currentParticipantId == perspectiveParticipantId) {
            return output[valueIndex];
        }

        return -output[valueIndex];
    }
}