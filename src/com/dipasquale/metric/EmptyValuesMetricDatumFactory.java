package com.dipasquale.metric;

import java.io.Serial;
import java.io.Serializable;

public final class EmptyValuesMetricDatumFactory implements MetricDatumFactory, Serializable {
    @Serial
    private static final long serialVersionUID = -3065896467580503014L;

    @Override
    public MetricDatum create() {
        return new EmptyValuesMetricDatum();
    }
}
