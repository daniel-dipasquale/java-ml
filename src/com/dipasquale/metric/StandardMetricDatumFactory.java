package com.dipasquale.metric;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class StandardMetricDatumFactory implements MetricDatumFactory, Serializable {
    @Serial
    private static final long serialVersionUID = -410616943583868217L;
    private static final StandardMetricDatumFactory INSTANCE = new StandardMetricDatumFactory();

    public static StandardMetricDatumFactory getInstance() {
        return INSTANCE;
    }

    @Override
    public MetricDatum create() {
        return new StandardMetricDatum();
    }

    @Serial
    private Object readResolve() {
        return INSTANCE;
    }
}
