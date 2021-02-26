package com.dipasquale.data.structure.probabilistic.count.min.sketch;

@FunctionalInterface
public interface RecycledCollector<T> {
    void collect(CountMinSketch<T> countMinSketch, long recycledDateTime);
}
