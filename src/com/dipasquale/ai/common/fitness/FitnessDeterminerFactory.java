/*
 * java-ml
 * (c) 2021 daniel-dipasquale
 * released under the MIT license
 */

package com.dipasquale.ai.common.fitness;

import com.dipasquale.common.ArgumentValidatorSupport;
import com.dipasquale.common.factory.ObjectFactory;
import com.dipasquale.metric.MetricDatum;

@FunctionalInterface
public interface FitnessDeterminerFactory extends ObjectFactory<FitnessDeterminer> {
    static FitnessDeterminerFactory createLastValue() {
        return new LastValueFitnessDeterminerFactory();
    }

    static FitnessDeterminerFactory createSum() {
        return new MetricDatumFitnessDeterminerFactory(MetricDatum::getSum);
    }

    static FitnessDeterminerFactory createAverage() {
        return new MetricDatumFitnessDeterminerFactory(MetricDatum::getAverage);
    }

    static FitnessDeterminerFactory createMinimum() {
        return new MetricDatumFitnessDeterminerFactory(MetricDatum::getMinimum);
    }

    static FitnessDeterminerFactory createMaximum() {
        return new MetricDatumFitnessDeterminerFactory(MetricDatum::getMaximum);
    }

    static FitnessDeterminerFactory createPth(final float percentage) {
        ArgumentValidatorSupport.ensureGreaterThanOrEqualTo(percentage, 0f, "percentage");
        ArgumentValidatorSupport.ensureLessThanOrEqualTo(percentage, 1f, "percentage");

        if (Float.compare(percentage, 0f) == 0) {
            return createMinimum();
        }

        if (Float.compare(percentage, 1f) == 0) {
            return createMaximum();
        }

        return new MetricDatumFitnessDeterminerFactory(md -> md.getPth(percentage));
    }
}
