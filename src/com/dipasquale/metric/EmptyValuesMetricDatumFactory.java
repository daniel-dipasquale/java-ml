package com.dipasquale.metric;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class EmptyValuesMetricDatumFactory implements MetricDatumFactory, Serializable {
    @Serial
    private static final long serialVersionUID = -3065896467580503014L;
    private static final EmptyValuesMetricDatumFactory INSTANCE = new EmptyValuesMetricDatumFactory();

    public static EmptyValuesMetricDatumFactory getInstance() {
        return INSTANCE;
    }

    @Override
    public MetricDatum create() {
        return new EmptyValuesMetricDatum();
    }

    @Serial
    private Object readResolve() {
        return INSTANCE;
    }
}
