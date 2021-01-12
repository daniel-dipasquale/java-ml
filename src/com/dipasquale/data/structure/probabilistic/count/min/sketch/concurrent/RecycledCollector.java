package com.dipasquale.data.structure.probabilistic.count.min.sketch.concurrent;

import com.dipasquale.data.structure.probabilistic.count.min.sketch.CountMinSketch;

@FunctionalInterface
public interface RecycledCollector<T> {
    void collect(CountMinSketch<T> countMinSketch, long recycledDateTime);
}
