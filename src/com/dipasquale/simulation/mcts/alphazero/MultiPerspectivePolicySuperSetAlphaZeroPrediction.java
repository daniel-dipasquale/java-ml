package com.dipasquale.simulation.mcts.alphazero;

final class MultiPerspectivePolicySuperSetAlphaZeroPrediction extends AbstractMultiPerspectivePolicySetAlphaZeroPrediction {
    private final int valueIndex;

    MultiPerspectivePolicySuperSetAlphaZeroPrediction(final int nextParticipantId, final int currentParticipantId, final int perspectiveParticipantId, final float[] output, final int valueIndex) {
        super(nextParticipantId, currentParticipantId, perspectiveParticipantId, output, valueIndex);
        this.valueIndex = valueIndex;
    }

    @Override
    protected int getPolicyIndex(final int index) {
        if (index < valueIndex) {
            return index;
        }

        return index + 1;
    }
}
