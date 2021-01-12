package com.experimental.metrics;

import javax.measure.unit.Unit;

public interface MetricKey {
    String getName();

    Unit<?> getUnit();
}
