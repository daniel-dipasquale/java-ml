package com.dipasquale.common.random;

import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@RequiredArgsConstructor
public final class GammaDistributionSupport implements UnivariateDistributionSupport, Serializable { // NOTE: copied from https://commons.apache.org/proper/commons-math/javadocs/api-3.6.1/src-html/org/apache/commons/math3/distribution/GammaDistribution.html
    @Serial
    private static final long serialVersionUID = -167845960195979338L;
    private static final double MEAN = 0D;
    private static final double STANDARD_DEVIATION = 1D;
    private final RandomSupport randomSupport;
    private final UnivariateDistributionSupport univariateDistributionSupport;

    private double generateNextRandom_shape_lessThanOne(final double shape, final double scale) {
        double b = 1D + shape / Math.E;

        while (true) {
            double u = randomSupport.nextDouble();
            double p = b * u;

            if (Double.compare(p, 1D) <= 0) {
                double x = Math.pow(p, 1D / shape);

                if (Double.compare(randomSupport.nextDouble(), Math.exp(-x)) <= 0) {
                    return scale * x;
                }
            } else {
                double x = -1D * Math.log((b - p) / shape);

                if (Double.compare(randomSupport.nextDouble(), Math.pow(x, shape - 1D)) <= 0) {
                    return scale * x;
                }
            }
        }
    }

    private double generateNextRandom_shape_greaterThanOrEqualToOne(final double shape, final double scale) {
        double d = shape - 0.333333333333333333D;
        double c = 1D / (3D * Math.sqrt(d));

        while (true) {
            double x = univariateDistributionSupport.nextRandom(MEAN, STANDARD_DEVIATION);
            double v = Math.pow(1D + c * x, 3D);

            if (Double.compare(v, 0D) > 0) {
                double u = randomSupport.nextDouble();
                double x2 = Math.pow(x, 2D);

                if (Double.compare(u, 1D - 0.0331D * Math.pow(x2, 2D)) < 0 || Double.compare(Math.log(u), 0.5D * x2 + d * (1 - v + Math.log(v))) < 0) {
                    return scale * d * v;
                }
            }
        }
    }

    private double generateNextRandom(final double shape, final double scale) {
        if (Double.compare(shape, 1D) < 0) {
            return generateNextRandom_shape_lessThanOne(shape, scale);
        }

        return generateNextRandom_shape_greaterThanOrEqualToOne(shape, scale);
    }

    @Override
    public double nextRandom(final double shape, final double scale) {
        return generateNextRandom(Math.abs(shape), Math.abs(scale));
    }
}
