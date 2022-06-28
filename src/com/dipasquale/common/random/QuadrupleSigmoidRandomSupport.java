package com.dipasquale.common.random;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

public final class QuadrupleSigmoidRandomSupport implements RandomSupport, Serializable {
    @Serial
    private static final long serialVersionUID = -4181447142293325129L;
    private static final float MAXIMUM_SAFE_FLOAT_VALUE_LESS_THAN_ONE = Float.intBitsToFloat(Float.floatToRawIntBits(1f) - 1);
    private static final double MAXIMUM_SAFE_DOUBLE_VALUE_LESS_THAN_ONE = Double.longBitsToDouble(Double.doubleToRawLongBits(1D) - 1L);
    private static final double SINGLE_SIGMOID_RANGE = 0.5D;
    private final RandomSupport randomSupport;
    private final double plateauAreaRate;
    private final double plateauScaleRate;
    private final double plateauSteepenedRate;
    private final double exponentialGrowthScaleRate;
    private final double exponentialGrowthSteepenedRate;
    private final float minimumFloat;
    private final float maximumFloat;
    private final double minimumDouble;
    private final double maximumDouble;

    private static double calculateSigmoid(final double scale, final double value, final double steepenedRate) {
        return scale / (1D + Math.exp(-steepenedRate * value + 0.5D * steepenedRate));
    }

    private static double calculateDoubleSigmoid(final double value, final double plateauScaleRate, final double plateauSteepenedRate, final double exponentialGrowthScaleRate, final double exponentialGrowthSteepenedRate) {
        double sigmoid1 = calculateSigmoid(plateauScaleRate, value, plateauSteepenedRate);
        double sigmoid2 = calculateSigmoid(exponentialGrowthScaleRate, value, exponentialGrowthSteepenedRate);

        return sigmoid1 + sigmoid2;
    }

    private static double calculateQuadrupleSigmoid(final double value, final double plateauAreaRate, final double plateauScaleRate, final double plateauSteepenedRate, final double exponentialGrowthScaleRate, final double exponentialGrowthSteepenedRate) {
        double doubleSigmoid1 = calculateDoubleSigmoid(value - plateauAreaRate, plateauScaleRate, plateauSteepenedRate, exponentialGrowthScaleRate, exponentialGrowthSteepenedRate);
        double doubleSigmoid2 = calculateDoubleSigmoid(value + plateauAreaRate, plateauScaleRate, plateauSteepenedRate, exponentialGrowthScaleRate, exponentialGrowthSteepenedRate);

        return doubleSigmoid1 + doubleSigmoid2;
    }

    private static Params calculateParams(final double plateauAreaRate, final double plateauRangeRate, final double steepenedRate) {
        double fixedPlateauAreaRate = plateauAreaRate * 0.5D;
        double plateauScaleRate = SINGLE_SIGMOID_RANGE * plateauRangeRate;
        double plateauSteepenedRate = 0.25D * Math.pow(steepenedRate, 0.5D);
        double exponentialGrowthScaleRate = SINGLE_SIGMOID_RANGE * (1D - plateauRangeRate);

        return Params.builder()
                .plateauAreaRate(fixedPlateauAreaRate)
                .plateauScaleRate(plateauScaleRate)
                .plateauSteepenedRate(plateauSteepenedRate)
                .exponentialGrowthScaleRate(exponentialGrowthScaleRate)
                .exponentialGrowthSteepenedRate(steepenedRate)
                .minimumValue(calculateQuadrupleSigmoid(0D, fixedPlateauAreaRate, plateauScaleRate, plateauSteepenedRate, exponentialGrowthScaleRate, steepenedRate))
                .maximumValue(calculateQuadrupleSigmoid(1D, fixedPlateauAreaRate, plateauScaleRate, plateauSteepenedRate, exponentialGrowthScaleRate, steepenedRate))
                .build();
    }

    private QuadrupleSigmoidRandomSupport(final RandomSupport randomSupport, final Params params) {
        this.randomSupport = randomSupport;
        this.plateauAreaRate = params.plateauAreaRate;
        this.plateauScaleRate = params.plateauScaleRate;
        this.plateauSteepenedRate = params.plateauSteepenedRate;
        this.exponentialGrowthScaleRate = params.exponentialGrowthScaleRate;
        this.exponentialGrowthSteepenedRate = params.exponentialGrowthSteepenedRate;
        this.minimumFloat = (float) params.minimumValue;
        this.maximumFloat = (float) params.maximumValue;
        this.minimumDouble = params.minimumValue;
        this.maximumDouble = params.maximumValue;
    }

    public QuadrupleSigmoidRandomSupport(final RandomSupport randomSupport, final double plateauAreaRate, final double plateauRangeRate, final double steepenedRate) {
        this(randomSupport, calculateParams(plateauAreaRate, plateauRangeRate, steepenedRate));
    }

    private double calculateQuadrupleSigmoid(final double x) {
        return calculateQuadrupleSigmoid(x, plateauAreaRate, plateauScaleRate, plateauSteepenedRate, exponentialGrowthScaleRate, exponentialGrowthSteepenedRate);
    }

    private float prorate(final float value) {
        return (value - minimumFloat) / (maximumFloat - minimumFloat) * MAXIMUM_SAFE_FLOAT_VALUE_LESS_THAN_ONE;
    }

    @Override
    public float nextFloat() {
        float value = (float) calculateQuadrupleSigmoid(randomSupport.nextFloat());

        return prorate(value);
    }

    private double prorate(final double value) {
        return (value - minimumDouble) / (maximumDouble - minimumDouble) * MAXIMUM_SAFE_DOUBLE_VALUE_LESS_THAN_ONE;
    }

    @Override
    public double nextDouble() {
        double value = calculateQuadrupleSigmoid(randomSupport.nextDouble());

        return prorate(value);
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    @Builder
    private static final class Params {
        private final double plateauAreaRate;
        private final double plateauScaleRate;
        private final double plateauSteepenedRate;
        private final double exponentialGrowthScaleRate;
        private final double exponentialGrowthSteepenedRate;
        private final double minimumValue;
        private final double maximumValue;
    }
}
