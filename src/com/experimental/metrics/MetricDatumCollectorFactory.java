package com.experimental.metrics;

import com.dipasquale.common.factory.ObjectFactory;

@FunctionalInterface
public interface MetricDatumCollectorFactory<T extends MetricDatumCollector> extends ObjectFactory<T> {
}
