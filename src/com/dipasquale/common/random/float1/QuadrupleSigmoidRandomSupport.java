package com.dipasquale.common.random.float1;

import java.io.Serial;
import java.io.Serializable;

public final class QuadrupleSigmoidRandomSupport implements RandomSupport, Serializable {
    @Serial
    private static final long serialVersionUID = -4181447142293325129L;
    private static final float MAX_VALUE_LESS_THAN_ONE = Float.intBitsToFloat(Float.floatToRawIntBits(1f) - 1);
    private static final float SINGLE_SIGMOID_BOUNDARY = 0.5f;
    private final RandomSupport randomSupport;
    private final float plateauAreaRatio;
    private final float plateauBoundaryRatio;
    private final float plateauSteepenedRate;
    private final float exponentialGrowthBoundaryRatio;
    private final float exponentialGrowthSteepenedRate;
    private final float minimumValue;
    private final float maximumValue;

    public QuadrupleSigmoidRandomSupport(final RandomSupport randomSupport, final float plateauAreaRatio, final float plateauRangeRatio, final float steepenedRate) {
        float plateauAreaRatioFixed = plateauAreaRatio * 0.5f;
        float plateauBoundaryRatio = SINGLE_SIGMOID_BOUNDARY * plateauRangeRatio;
        float plateauSteepenedRate = 0.25f * (float) Math.pow(steepenedRate, 0.5f);
        float exponentialGrowthBoundaryRatio = SINGLE_SIGMOID_BOUNDARY * (1f - plateauRangeRatio);

        this.randomSupport = randomSupport;
        this.plateauAreaRatio = plateauAreaRatioFixed;
        this.plateauBoundaryRatio = plateauBoundaryRatio;
        this.plateauSteepenedRate = plateauSteepenedRate;
        this.exponentialGrowthBoundaryRatio = exponentialGrowthBoundaryRatio;
        this.exponentialGrowthSteepenedRate = steepenedRate;
        this.minimumValue = calculateQuadrupleSigmoid(0f, plateauAreaRatioFixed, plateauBoundaryRatio, plateauSteepenedRate, exponentialGrowthBoundaryRatio, steepenedRate);
        this.maximumValue = calculateQuadrupleSigmoid(1f, plateauAreaRatioFixed, plateauBoundaryRatio, plateauSteepenedRate, exponentialGrowthBoundaryRatio, steepenedRate);
    }

    private static float calculateSigmoid(final float boundary, final float value, final float steepenedRate) {
        return boundary / (1f + (float) Math.exp(-steepenedRate * value + 0.5f * steepenedRate));
    }

    private static float calculateDoubleSigmoid(final float value, final float plateauBoundaryRatio, final float plateauSteepenedRate, final float exponentialGrowthBoundaryRatio, final float exponentialGrowthSteepenedRate) {
        float sigmoid1 = calculateSigmoid(plateauBoundaryRatio, value, plateauSteepenedRate);
        float sigmoid2 = calculateSigmoid(exponentialGrowthBoundaryRatio, value, exponentialGrowthSteepenedRate);

        return sigmoid1 + sigmoid2;
    }

    private static float calculateQuadrupleSigmoid(final float value, final float plateauAreaRatio, final float plateauBoundaryRatio, final float plateauSteepenedRate, final float exponentialGrowthBoundaryRatio, final float exponentialGrowthSteepenedRate) {
        float doubleSigmoid1 = calculateDoubleSigmoid(value - plateauAreaRatio, plateauBoundaryRatio, plateauSteepenedRate, exponentialGrowthBoundaryRatio, exponentialGrowthSteepenedRate);
        float doubleSigmoid2 = calculateDoubleSigmoid(value + plateauAreaRatio, plateauBoundaryRatio, plateauSteepenedRate, exponentialGrowthBoundaryRatio, exponentialGrowthSteepenedRate);

        return doubleSigmoid1 + doubleSigmoid2;
    }

    private static float calculate(final float value, final float min, final float max) {
        return (value - min) / (max - min) * MAX_VALUE_LESS_THAN_ONE;
    }

    @Override
    public float next() {
        float x = randomSupport.next();
        float value = calculateQuadrupleSigmoid(x, plateauAreaRatio, plateauBoundaryRatio, plateauSteepenedRate, exponentialGrowthBoundaryRatio, exponentialGrowthSteepenedRate);

        return calculate(value, minimumValue, maximumValue);
    }
}
