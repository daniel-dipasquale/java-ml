package com.dipasquale.metric;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class LightMetricDatumFactory implements MetricDatumFactory, Serializable {
    @Serial
    private static final long serialVersionUID = -3065896467580503014L;
    private static final LightMetricDatumFactory INSTANCE = new LightMetricDatumFactory();

    public static LightMetricDatumFactory getInstance() {
        return INSTANCE;
    }

    @Override
    public MetricDatum create() {
        return new LightMetricDatum();
    }

    @Serial
    private Object readResolve() {
        return INSTANCE;
    }
}
