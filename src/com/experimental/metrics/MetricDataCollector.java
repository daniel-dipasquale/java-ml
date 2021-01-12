package com.experimental.metrics;

import javax.measure.unit.Unit;
import java.util.List;

public interface MetricDataCollector {
    void add(String name, double value, Unit<?> unit);

    List<MetricData> flush(boolean force);
}
