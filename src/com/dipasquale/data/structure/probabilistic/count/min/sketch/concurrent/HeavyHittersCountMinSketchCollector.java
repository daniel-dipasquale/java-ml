package com.dipasquale.data.structure.probabilistic.count.min.sketch.concurrent;

import com.dipasquale.data.structure.probabilistic.count.min.sketch.CountMinSketch;

import java.util.List;

@FunctionalInterface
public interface HeavyHittersCountMinSketchCollector<T> {
    void collect(CountMinSketch<T> countMinSketch, long recycledDateTime, List<T> heavyHitters);
}
