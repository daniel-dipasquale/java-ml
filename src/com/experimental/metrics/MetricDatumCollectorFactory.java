package com.experimental.metrics;

import com.dipasquale.common.ObjectFactory;

@FunctionalInterface
public interface MetricDatumCollectorFactory<T extends MetricDatumCollector> extends ObjectFactory<T> {
}
