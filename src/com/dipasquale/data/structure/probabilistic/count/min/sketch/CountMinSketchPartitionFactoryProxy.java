package com.dipasquale.data.structure.probabilistic.count.min.sketch;

@FunctionalInterface
public interface CountMinSketchPartitionFactoryProxy {
    <T> CountMinSketch<T> create(int index);
}
