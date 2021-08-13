/*
 * java-ml
 * (c) 2021 daniel-dipasquale
 * released under the MIT license
 */

package com.dipasquale.data.structure.probabilistic.bloom.filter;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

final class MultiPartBloomFilter<T> implements BloomFilter<T>, Serializable {
    @Serial
    private static final long serialVersionUID = 3882809450291604624L;
    private final List<BloomFilter<T>> bloomFilters;

    MultiPartBloomFilter(final BloomFilterPartitionFactory.Proxy bloomFilterPartitionFactoryProxy, final int count) {
        this.bloomFilters = IntStream.range(0, count)
                .mapToObj(bloomFilterPartitionFactoryProxy::<T>create)
                .collect(Collectors.toList());
    }

    private BloomFilter<T> getBloomFilter(final T item) {
        int hashCode = Math.abs(item.hashCode());

        return bloomFilters.get(hashCode % bloomFilters.size());
    }

    @Override
    public boolean mightContain(final T item) {
        return getBloomFilter(item).mightContain(item);
    }

    @Override
    public boolean add(final T item) {
        return getBloomFilter(item).add(item);
    }
}
