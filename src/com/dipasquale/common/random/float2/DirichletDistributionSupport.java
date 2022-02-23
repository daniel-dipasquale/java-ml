package com.dipasquale.common.random.float2;

import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@RequiredArgsConstructor
public final class DirichletDistributionSupport implements MultivariateDistributionSupport, Serializable {
    @Serial
    private static final long serialVersionUID = -4601914023863014458L;
    private static final double SCALE = 1D;
    private final UnivariateDistributionSupport univariateDistributionSupport;

    @Override
    public double[] nextRandom(final double[] shapes) {
        double[] result = new double[shapes.length];
        double total = 0D;

        for (int i = 0; i < result.length; i++) {
            result[i] = univariateDistributionSupport.nextRandom(shapes[i], SCALE);
            total += result[i];
        }

        if (Double.compare(total, 0D) == 0) {
            result[0] = 1D;
            total = 1D;
        }

        for (int i = 0; i < result.length; i++) {
            result[i] /= total;
        }

        return result;
    }
}
