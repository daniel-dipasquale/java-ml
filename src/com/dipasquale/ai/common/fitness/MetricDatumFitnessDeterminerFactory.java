package com.dipasquale.ai.common.fitness;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class MetricDatumFitnessDeterminerFactory implements FitnessDeterminerFactory, Serializable {
    @Serial
    private static final long serialVersionUID = -2840305259809237496L;
    private final MetricDatumSelector metricDatumSelector;

    @Override
    public FitnessDeterminer create() {
        return new MetricDatumFitnessDeterminer(metricDatumSelector);
    }
}
