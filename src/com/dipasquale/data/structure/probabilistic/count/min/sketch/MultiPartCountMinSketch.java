package com.dipasquale.data.structure.probabilistic.count.min.sketch;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

final class MultiPartCountMinSketch<T> implements CountMinSketch<T>, Serializable {
    @Serial
    private static final long serialVersionUID = -7604605815397971535L;
    private final List<CountMinSketch<T>> countMinSketches;

    MultiPartCountMinSketch(final CountMinSketchPartitionFactory.Proxy countMinSketchPartitionFactoryProxy, final int count) {
        this.countMinSketches = IntStream.range(0, count)
                .mapToObj(countMinSketchPartitionFactoryProxy::<T>create)
                .collect(Collectors.toList());
    }

    private CountMinSketch<T> getCountMinSketch(final T item) {
        int hashCode = Math.abs(item.hashCode());

        return countMinSketches.get(hashCode % countMinSketches.size());
    }

    @Override
    public long get(final T item) {
        return getCountMinSketch(item).get(item);
    }

    @Override
    public long put(final T item, final long count) {
        return getCountMinSketch(item).put(item, count);
    }
}
