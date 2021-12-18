package com.dipasquale.common.random.float2;

public final class QuadrupleSigmoidRandomSupport implements RandomSupport {
    private static final double MAX_VALUE_LESS_THAN_ONE = Double.longBitsToDouble(Double.doubleToRawLongBits(1D) - 1L);
    private static final double SINGLE_SIGMOID_BOUNDARY = 0.5D;
    private final RandomSupport randomSupport;
    private final double plateauAreaRatio;
    private final double plateauBoundaryRatio;
    private final double plateauSteepenedRate;
    private final double exponentialGrowthBoundaryRatio;
    private final double exponentialGrowthSteepenedRate;
    private final double minimumValue;
    private final double maximumValue;

    public QuadrupleSigmoidRandomSupport(final RandomSupport randomSupport, final double plateauAreaRatio, final double plateauRangeRatio, final double steepenedRate) {
        double plateauAreaRatioFixed = plateauAreaRatio * 0.5D;
        double plateauBoundaryRatio = SINGLE_SIGMOID_BOUNDARY * plateauRangeRatio;
        double plateauSteepenedRate = 0.25D * Math.pow(steepenedRate, 0.5D);
        double exponentialGrowthBoundaryRatio = SINGLE_SIGMOID_BOUNDARY * (1D - plateauRangeRatio);

        this.randomSupport = randomSupport;
        this.plateauAreaRatio = plateauAreaRatioFixed;
        this.plateauBoundaryRatio = plateauBoundaryRatio;
        this.plateauSteepenedRate = plateauSteepenedRate;
        this.exponentialGrowthBoundaryRatio = exponentialGrowthBoundaryRatio;
        this.exponentialGrowthSteepenedRate = steepenedRate;
        this.minimumValue = calculateQuadrupleSigmoid(0D, plateauAreaRatioFixed, plateauBoundaryRatio, plateauSteepenedRate, exponentialGrowthBoundaryRatio, steepenedRate);
        this.maximumValue = calculateQuadrupleSigmoid(1D, plateauAreaRatioFixed, plateauBoundaryRatio, plateauSteepenedRate, exponentialGrowthBoundaryRatio, steepenedRate);
    }

    private static double calculateSigmoid(final double boundary, final double value, final double steepenedRate) {
        return boundary / (1D + Math.exp(-steepenedRate * value + 0.5D * steepenedRate));
    }

    private static double calculateDoubleSigmoid(final double value, final double plateauBoundaryRatio, final double plateauSteepenedRate, final double exponentialGrowthBoundaryRatio, final double exponentialGrowthSteepenedRate) {
        double sigmoid1 = calculateSigmoid(plateauBoundaryRatio, value, plateauSteepenedRate);
        double sigmoid2 = calculateSigmoid(exponentialGrowthBoundaryRatio, value, exponentialGrowthSteepenedRate);

        return sigmoid1 + sigmoid2;
    }

    private static double calculateQuadrupleSigmoid(final double value, final double plateauAreaRatio, final double plateauBoundaryRatio, final double plateauSteepenedRate, final double exponentialGrowthBoundaryRatio, final double exponentialGrowthSteepenedRate) {
        double doubleSigmoid1 = calculateDoubleSigmoid(value - plateauAreaRatio, plateauBoundaryRatio, plateauSteepenedRate, exponentialGrowthBoundaryRatio, exponentialGrowthSteepenedRate);
        double doubleSigmoid2 = calculateDoubleSigmoid(value + plateauAreaRatio, plateauBoundaryRatio, plateauSteepenedRate, exponentialGrowthBoundaryRatio, exponentialGrowthSteepenedRate);

        return doubleSigmoid1 + doubleSigmoid2;
    }

    private static double calculate(final double value, final double min, final double max) {
        return (value - min) / (max - min) * MAX_VALUE_LESS_THAN_ONE;
    }

    @Override
    public double next() {
        double x = randomSupport.next();
        double value = calculateQuadrupleSigmoid(x, plateauAreaRatio, plateauBoundaryRatio, plateauSteepenedRate, exponentialGrowthBoundaryRatio, exponentialGrowthSteepenedRate);

        return calculate(value, minimumValue, maximumValue);
    }
}
