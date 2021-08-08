package com.dipasquale.data.structure.probabilistic.count.min.sketch.concurrent;

import com.dipasquale.data.structure.probabilistic.count.min.sketch.CountMinSketch;

@FunctionalInterface
public interface RecycledCountMinSketchCollector<T> {
    void collect(CountMinSketch<T> countMinSketch, long recycledDateTime);
}
