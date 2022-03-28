package com.dipasquale.common.random.float2;

import java.io.Serial;
import java.io.Serializable;

public final class QuadrupleSigmoidRandomSupport implements RandomSupport, Serializable {
    @Serial
    private static final long serialVersionUID = -8244386024819513849L;
    private static final double MAX_VALUE_LESS_THAN_ONE = Double.longBitsToDouble(Double.doubleToRawLongBits(1D) - 1L);
    private static final double SINGLE_SIGMOID_BOUNDARY = 0.5D;
    private final RandomSupport randomSupport;
    private final double plateauAreaRate;
    private final double plateauBoundaryRate;
    private final double plateauSteepenedRate;
    private final double exponentialGrowthBoundaryRate;
    private final double exponentialGrowthSteepenedRate;
    private final double minimumValue;
    private final double maximumValue;

    private static double calculateSigmoid(final double boundary, final double value, final double steepenedRate) {
        return boundary / (1D + Math.exp(-steepenedRate * value + 0.5D * steepenedRate));
    }

    private static double calculateDoubleSigmoid(final double value, final double plateauBoundaryRate, final double plateauSteepenedRate, final double exponentialGrowthBoundaryRate, final double exponentialGrowthSteepenedRate) {
        double sigmoid1 = calculateSigmoid(plateauBoundaryRate, value, plateauSteepenedRate);
        double sigmoid2 = calculateSigmoid(exponentialGrowthBoundaryRate, value, exponentialGrowthSteepenedRate);

        return sigmoid1 + sigmoid2;
    }

    private static double calculateQuadrupleSigmoid(final double value, final double plateauAreaRate, final double plateauBoundaryRate, final double plateauSteepenedRate, final double exponentialGrowthBoundaryRate, final double exponentialGrowthSteepenedRate) {
        double doubleSigmoid1 = calculateDoubleSigmoid(value - plateauAreaRate, plateauBoundaryRate, plateauSteepenedRate, exponentialGrowthBoundaryRate, exponentialGrowthSteepenedRate);
        double doubleSigmoid2 = calculateDoubleSigmoid(value + plateauAreaRate, plateauBoundaryRate, plateauSteepenedRate, exponentialGrowthBoundaryRate, exponentialGrowthSteepenedRate);

        return doubleSigmoid1 + doubleSigmoid2;
    }

    public QuadrupleSigmoidRandomSupport(final RandomSupport randomSupport, final double plateauAreaRate, final double plateauRangeRate, final double steepenedRate) {
        double fixedPlateauAreaRate = plateauAreaRate * 0.5D;
        double plateauBoundaryRate = SINGLE_SIGMOID_BOUNDARY * plateauRangeRate;
        double plateauSteepenedRate = 0.25D * Math.pow(steepenedRate, 0.5D);
        double exponentialGrowthBoundaryRate = SINGLE_SIGMOID_BOUNDARY * (1D - plateauRangeRate);

        this.randomSupport = randomSupport;
        this.plateauAreaRate = fixedPlateauAreaRate;
        this.plateauBoundaryRate = plateauBoundaryRate;
        this.plateauSteepenedRate = plateauSteepenedRate;
        this.exponentialGrowthBoundaryRate = exponentialGrowthBoundaryRate;
        this.exponentialGrowthSteepenedRate = steepenedRate;
        this.minimumValue = calculateQuadrupleSigmoid(0D, fixedPlateauAreaRate, plateauBoundaryRate, plateauSteepenedRate, exponentialGrowthBoundaryRate, steepenedRate);
        this.maximumValue = calculateQuadrupleSigmoid(1D, fixedPlateauAreaRate, plateauBoundaryRate, plateauSteepenedRate, exponentialGrowthBoundaryRate, steepenedRate);
    }

    private static double calculate(final double value, final double minimum, final double maximum) {
        return (value - minimum) / (maximum - minimum) * MAX_VALUE_LESS_THAN_ONE;
    }

    @Override
    public double next() {
        double x = randomSupport.next();
        double value = calculateQuadrupleSigmoid(x, plateauAreaRate, plateauBoundaryRate, plateauSteepenedRate, exponentialGrowthBoundaryRate, exponentialGrowthSteepenedRate);

        return calculate(value, minimumValue, maximumValue);
    }
}
