package com.dipasquale.simulation.mcts.alphazero;

import com.dipasquale.search.mcts.alphazero.AlphaZeroPrediction;
import com.dipasquale.search.mcts.core.Environment;
import com.dipasquale.search.mcts.core.State;

public final class MultiPerspectiveAlphaZeroPrediction<TState extends State, TEnvironment extends Environment<TState, TEnvironment>> implements AlphaZeroPrediction {
    private final int nextParticipantId;
    private final int currentParticipantId;
    private final int perspectiveParticipantId;
    private final boolean policySuperSet;
    private final float policyChoiceCount;
    private final float policyChoiceAvailableCount;
    private final int valueIndex;
    private final float[] output;

    MultiPerspectiveAlphaZeroPrediction(final int perspectiveParticipantId, final int valueIndex, final NeatAlphaZeroHeuristicContext<TState, TEnvironment> context, final float[] output) {
        TEnvironment environment = context.getEnvironment();
        int policyChoiceCount = context.getChildrenCount();
        int policyChoiceAvailableCount = output.length - 1;

        this.nextParticipantId = environment.getNextParticipantId();
        this.currentParticipantId = environment.getCurrentState().getParticipantId();
        this.perspectiveParticipantId = perspectiveParticipantId;
        this.policySuperSet = policyChoiceCount <= policyChoiceAvailableCount;
        this.policyChoiceCount = (float) policyChoiceCount;
        this.policyChoiceAvailableCount = (float) policyChoiceAvailableCount;
        this.valueIndex = valueIndex;
        this.output = output;
    }

    private int getIndex(final float index) {
        float value = index / policyChoiceCount;

        return (int) Math.floor(value * policyChoiceAvailableCount);
    }

    private int getPolicyIndex(final int index) {
        if (policySuperSet) {
            if (index < valueIndex) {
                return index;
            }

            return index + 1;
        }

        int indexFixed = getIndex((float) index);

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