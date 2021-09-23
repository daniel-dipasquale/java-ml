package com.dipasquale.ai.common.fitness;

import com.dipasquale.common.factory.ObjectFactory;
import com.dipasquale.metric.MetricDatum;

import java.io.Serializable;

@FunctionalInterface
public interface FitnessDeterminerFactory extends ObjectFactory<FitnessDeterminer> {
    static FitnessDeterminerFactory createLastValue() {
        return new LastValueFitnessDeterminerFactory();
    }

    static FitnessDeterminerFactory createSum() {
        return new MetricDatumFitnessDeterminerFactory((MetricDatumSelector & Serializable) MetricDatum::getSum);
    }

    static FitnessDeterminerFactory createAverage() {
        return new MetricDatumFitnessDeterminerFactory((MetricDatumSelector & Serializable) MetricDatum::getAverage);
    }

    static FitnessDeterminerFactory createMinimum() {
        return new MetricDatumFitnessDeterminerFactory((MetricDatumSelector & Serializable) MetricDatum::getMinimum);
    }

    static FitnessDeterminerFactory createMaximum() {
        return new MetricDatumFitnessDeterminerFactory((MetricDatumSelector & Serializable) MetricDatum::getMaximum);
    }

    static FitnessDeterminerFactory createPth(final float percentage) {
        return new MetricDatumFitnessDeterminerFactory((MetricDatumSelector & Serializable) md -> md.getPercentile(percentage));
    }
}
