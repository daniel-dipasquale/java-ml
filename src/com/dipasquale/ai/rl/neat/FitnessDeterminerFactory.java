package com.dipasquale.ai.rl.neat;

import com.dipasquale.ai.common.MetricDatum;
import com.dipasquale.common.ArgumentValidatorUtils;

@FunctionalInterface
interface FitnessDeterminerFactory {
    FitnessDeterminer create();

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
        ArgumentValidatorUtils.ensureGreaterThanOrEqualTo(percentage, 0f, "percentage");
        ArgumentValidatorUtils.ensureLessThanOrEqualTo(percentage, 1f, "percentage");

        return () -> new FitnessDeterminerMetricDatum(md -> md.getPth(percentage));
    }
}
