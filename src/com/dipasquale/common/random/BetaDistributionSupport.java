package com.dipasquale.common.random;

import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@RequiredArgsConstructor
public final class BetaDistributionSupport implements UnivariateDistributionSupport, Serializable { // NOTE: copied from https://commons.apache.org/proper/commons-math/javadocs/api-3.6.1/src-html/org/apache/commons/math3/distribution/BetaDistribution.html
    @Serial
    private static final long serialVersionUID = -8788304157884783715L;
    private static final long APPROXIMATION_MASK = 0x8000000000000000L;
    private static final int NUMBER_OF_FLOATING_POINTS = 1;
    private static final long POSITIVE_ZERO_BITS = Double.doubleToRawLongBits(0D);
    private static final long NEGATIVE_ZERO_BITS = Double.doubleToRawLongBits(-0D);
    private final RandomSupport randomSupport;

    private static boolean approximates(final double x, final double y) {
        if (Double.isNaN(x) || Double.isNaN(y)) {
            return false;
        }

        long fixedX = Double.doubleToRawLongBits(x);
        long fixedY = Double.doubleToRawLongBits(y);

        if (((fixedX ^ fixedY) & APPROXIMATION_MASK) == 0L) {
            return Math.abs(fixedX - fixedY) <= NUMBER_OF_FLOATING_POINTS;
        }

        long deltaPlus;
        long deltaMinus;

        if (fixedX < fixedY) {
            deltaPlus = fixedY - POSITIVE_ZERO_BITS;
            deltaMinus = fixedX - NEGATIVE_ZERO_BITS;
        } else {
            deltaPlus = fixedX - POSITIVE_ZERO_BITS;
            deltaMinus = fixedY - NEGATIVE_ZERO_BITS;
        }

        if (deltaPlus > NUMBER_OF_FLOATING_POINTS) {
            return false;
        }

        return deltaMinus <= NUMBER_OF_FLOATING_POINTS - deltaPlus;
    }

    private double generateNextRandom_both_greaterThanOne(final double a0, final double a, final double b) {
        double alpha = a + b;
        double beta = Math.sqrt((alpha - 2D) / (2D * a * b - alpha));
        double gamma = a + 1D / beta;
        double r;
        double w;
        double t;

        do {
            double u1 = randomSupport.nextDouble();
            double u2 = randomSupport.nextDouble();
            double v = beta * (Math.log(u1) - Math.log1p(-u1));
            double z = Math.pow(u1, 2D) * u2;

            r = gamma * v - 1.3862944D;
            w = a * Math.exp(v);

            double s = a + r - w;

            if (Double.compare(s + 2.609438D, 5D * z) >= 0) {
                break;
            }

            t = Math.log(z);

            if (s >= t) {
                break;
            }
        } while (Double.compare(r + alpha * (Math.log(alpha) - Math.log(b + w)), t) < 0);

        w = Math.min(w, Double.MAX_VALUE);

        if (approximates(a, a0)) {
            return w / (b + w);
        }

        return b / (b + w);
    }

    private static double calculateValue(final double beta, final double random) {
        return beta * (Math.log(random) - Math.log1p(-random));
    }

    private double generateNextRandom_either_lessThanOrEqualToOne(final double a0, final double a, final double b) {
        double alpha = a + b;
        double beta = 1D / b;
        double delta = 1D + a - b;
        double k1 = delta * (0.0138889D + 0.0416667D * b) / (a * beta - 0.777778D);
        double k2 = 0.25D + (0.5D + 0.25D / delta) * b;
        double w;

        while (true) {
            double u1 = randomSupport.nextDouble();
            double u2 = randomSupport.nextDouble();
            double y = u1 * u2;
            double z = u1 * y;

            if (Double.compare(u1, 0.5D) >= 0) {
                if (Double.compare(z, 0.25D) <= 0) {
                    w = a * Math.exp(calculateValue(beta, u1));

                    break;
                }

                if (Double.compare(z, k2) >= 0) {
                    continue;
                }
            } else if (Double.compare(0.25D * u2 + z - y, k1) >= 0) {
                continue;
            }

            double v = calculateValue(beta, u1);

            w = a * Math.exp(v);

            if (Double.compare(alpha * (Math.log(alpha) - Math.log(b + w) + v) - 1.3862944D, Math.log(z)) >= 0) {
                break;
            }
        }

        w = Math.min(w, Double.MAX_VALUE);

        if (approximates(a, a0)) {
            return w / (b + w);
        }

        return b / (b + w);
    }

    private double generateNextRandom(final double alpha, final double beta) {
        double a = Math.min(alpha, beta);
        double b = Math.max(alpha, beta);

        if (Double.compare(a, 1D) > 0) {
            return generateNextRandom_both_greaterThanOne(alpha, a, b);
        }

        return generateNextRandom_either_lessThanOrEqualToOne(alpha, b, a);
    }

    @Override
    public double nextRandom(final double alpha, final double beta) {
        return generateNextRandom(Math.abs(alpha), Math.abs(beta));
    }
}
