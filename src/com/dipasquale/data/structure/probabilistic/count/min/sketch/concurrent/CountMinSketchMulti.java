package com.dipasquale.data.structure.probabilistic.count.min.sketch.concurrent;

import com.dipasquale.data.structure.probabilistic.count.min.sketch.CountMinSketch;
import com.dipasquale.data.structure.probabilistic.count.min.sketch.CountMinSketchPartitionFactory;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

final class CountMinSketchMulti<T> implements CountMinSketch<T> {
    private final List<CountMinSketch<T>> countMinSketches;

    CountMinSketchMulti(final CountMinSketchPartitionFactory.Proxy countMinSketchPartitionFactoryProxy, final int count) {
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
