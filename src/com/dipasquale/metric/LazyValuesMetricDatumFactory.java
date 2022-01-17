package com.dipasquale.metric;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class LazyValuesMetricDatumFactory implements MetricDatumFactory, Serializable {
    @Serial
    private static final long serialVersionUID = -410616943583868217L;
    private static final LazyValuesMetricDatumFactory INSTANCE = new LazyValuesMetricDatumFactory();

    public static LazyValuesMetricDatumFactory getInstance() {
        return INSTANCE;
    }

    @Serial
    private Object readResolve() {
        return INSTANCE;
    }

    @Override
    public MetricDatum create() {
        return new LazyValuesMetricDatum();
    }
}
