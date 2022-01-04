package com.dipasquale.simulation.mcts.alphazero;

final class MultiPerspectivePolicySubSetAlphaZeroPrediction extends AbstractMultiPerspectivePolicySetAlphaZeroPrediction {
    private static final float MAX_VALUE_LESS_THAN_ONE = Float.intBitsToFloat(Float.floatToRawIntBits(1f) - 1);
    private final float[] output;
    private final int valueIndex;

    MultiPerspectivePolicySubSetAlphaZeroPrediction(final int nextParticipantId, final int currentParticipantId, final int perspectiveParticipantId, final float[] output, final int valueIndex) {
        super(nextParticipantId, currentParticipantId, perspectiveParticipantId, output, valueIndex);
        this.output = output;
        this.valueIndex = valueIndex;
    }

    private static int getIndex(final float index, final float max) {
        float total = max - MAX_VALUE_LESS_THAN_ONE;
        float value = index / total;

        return (int) Math.floor(value * max);
    }

    @Override
    protected int getPolicyIndex(final int index) {
        int indexFixed = getIndex((float) index, (float) (output.length - 1));

        if (indexFixed < valueIndex) {
            return indexFixed;
        }

        return indexFixed + 1;
    }
}
