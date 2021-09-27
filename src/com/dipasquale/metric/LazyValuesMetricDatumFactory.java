package com.dipasquale.metric;

import java.io.Serial;
import java.io.Serializable;

public final class LazyValuesMetricDatumFactory implements MetricDatumFactory, Serializable {
    @Serial
    private static final long serialVersionUID = -410616943583868217L;

    @Override
    public MetricDatum create() {
        return new LazyValuesMetricDatum();
    }
}
