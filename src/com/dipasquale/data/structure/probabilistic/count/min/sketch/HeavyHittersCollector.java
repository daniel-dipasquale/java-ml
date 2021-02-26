package com.dipasquale.data.structure.probabilistic.count.min.sketch;

import java.util.List;

@FunctionalInterface
public interface HeavyHittersCollector<T> {
    void collect(CountMinSketch<T> countMinSketch, long recycledDateTime, List<T> heavyHitters);
}
