package com.dipasquale.common.random.float1;

import java.io.Serial;
import java.io.Serializable;

public final class QuadrupleSigmoidRandomSupport implements RandomSupport, Serializable {
    @Serial
    private static final long serialVersionUID = -4181447142293325129L;
    private static final float MAXIMUM_VALUE_LESS_THAN_ONE = Float.intBitsToFloat(Float.floatToRawIntBits(1f) - 1);
    private static final float SINGLE_SIGMOID_BOUNDARY = 0.5f;
    private final RandomSupport randomSupport;
    private final float plateauAreaRate;
    private final float plateauBoundaryRate;
    private final float plateauSteepenedRate;
    private final float exponentialGrowthBoundaryRate;
    private final float exponentialGrowthSteepenedRate;
    private final float minimumValue;
    private final float maximumValue;

    public QuadrupleSigmoidRandomSupport(final RandomSupport randomSupport, final float plateauAreaRate, final float plateauRangeRate, final float steepenedRate) {
        float plateauAreaRateFixed = plateauAreaRate * 0.5f;
        float plateauBoundaryRate = SINGLE_SIGMOID_BOUNDARY * plateauRangeRate;
        float plateauSteepenedRate = 0.25f * (float) Math.pow(steepenedRate, 0.5f);
        float exponentialGrowthBoundaryRate = SINGLE_SIGMOID_BOUNDARY * (1f - plateauRangeRate);

        this.randomSupport = randomSupport;
        this.plateauAreaRate = plateauAreaRateFixed;
        this.plateauBoundaryRate = plateauBoundaryRate;
        this.plateauSteepenedRate = plateauSteepenedRate;
        this.exponentialGrowthBoundaryRate = exponentialGrowthBoundaryRate;
        this.exponentialGrowthSteepenedRate = steepenedRate;
        this.minimumValue = calculateQuadrupleSigmoid(0f, plateauAreaRateFixed, plateauBoundaryRate, plateauSteepenedRate, exponentialGrowthBoundaryRate, steepenedRate);
        this.maximumValue = calculateQuadrupleSigmoid(1f, plateauAreaRateFixed, plateauBoundaryRate, plateauSteepenedRate, exponentialGrowthBoundaryRate, steepenedRate);
    }

    private static float calculateSigmoid(final float boundary, final float value, final float steepenedRate) {
        return boundary / (1f + (float) Math.exp(-steepenedRate * value + 0.5f * steepenedRate));
    }

    private static float calculateDoubleSigmoid(final float value, final float plateauBoundaryRate, final float plateauSteepenedRate, final float exponentialGrowthBoundaryRate, final float exponentialGrowthSteepenedRate) {
        float sigmoid1 = calculateSigmoid(plateauBoundaryRate, value, plateauSteepenedRate);
        float sigmoid2 = calculateSigmoid(exponentialGrowthBoundaryRate, value, exponentialGrowthSteepenedRate);

        return sigmoid1 + sigmoid2;
    }

    private static float calculateQuadrupleSigmoid(final float value, final float plateauAreaRate, final float plateauBoundaryRate, final float plateauSteepenedRate, final float exponentialGrowthBoundaryRate, final float exponentialGrowthSteepenedRate) {
        float doubleSigmoid1 = calculateDoubleSigmoid(value - plateauAreaRate, plateauBoundaryRate, plateauSteepenedRate, exponentialGrowthBoundaryRate, exponentialGrowthSteepenedRate);
        float doubleSigmoid2 = calculateDoubleSigmoid(value + plateauAreaRate, plateauBoundaryRate, plateauSteepenedRate, exponentialGrowthBoundaryRate, exponentialGrowthSteepenedRate);

        return doubleSigmoid1 + doubleSigmoid2;
    }

    private static float calculate(final float value, final float minimum, final float maximum) {
        return (value - minimum) / (maximum - minimum) * MAXIMUM_VALUE_LESS_THAN_ONE;
    }

    @Override
    public float next() {
        float x = randomSupport.next();
        float value = calculateQuadrupleSigmoid(x, plateauAreaRate, plateauBoundaryRate, plateauSteepenedRate, exponentialGrowthBoundaryRate, exponentialGrowthSteepenedRate);

        return calculate(value, minimumValue, maximumValue);
    }
}
