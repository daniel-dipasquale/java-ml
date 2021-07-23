package com.dipasquale.ai.common;

import com.dipasquale.common.ArgumentValidatorSupport;
import com.dipasquale.common.ObjectFactory;

@FunctionalInterface
public interface FitnessDeterminerFactory extends ObjectFactory<FitnessDeterminer> {
    static FitnessDeterminerFactory createLastValueFactory() {
        return FitnessDeterminerSingle::new;
    }

    static FitnessDeterminerFactory createSumFactory() {
        return () -> new FitnessDeterminerMetricDatum(MetricDatum::getSum);
    }

    static FitnessDeterminerFactory createAverageFactory() {
        return () -> new FitnessDeterminerMetricDatum(MetricDatum::getAverage);
    }

    static FitnessDeterminerFactory createMinimumFactory() {
        return () -> new FitnessDeterminerMetricDatum(MetricDatum::getMinimum);
    }

    static FitnessDeterminerFactory createMaximumFactory() {
        return () -> new FitnessDeterminerMetricDatum(MetricDatum::getMaximum);
    }

    static FitnessDeterminerFactory createPthFactory(final float percentage) {
        ArgumentValidatorSupport.ensureGreaterThanOrEqualTo(percentage, 0f, "percentage");
        ArgumentValidatorSupport.ensureLessThanOrEqualTo(percentage, 1f, "percentage");

        return () -> new FitnessDeterminerMetricDatum(md -> md.getPth(percentage));
    }
}
