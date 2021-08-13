/*
 * java-ml
 * (c) 2021 daniel-dipasquale
 * released under the MIT license
 */

package com.dipasquale.data.structure.probabilistic.bloom.filter.concurrent;

import com.dipasquale.common.concurrent.AtomicRecyclableReference;
import com.dipasquale.common.factory.ObjectFactory;
import com.dipasquale.common.time.ExpirationFactory;
import com.dipasquale.data.structure.probabilistic.bloom.filter.BloomFilter;

import java.io.Serial;
import java.io.Serializable;

final class RecyclableBloomFilter<T> implements BloomFilter<T>, Serializable {
    @Serial
    private static final long serialVersionUID = -3669803275544446308L;
    private final AtomicRecyclableReference<BloomFilter<T>> recyclableBloomFilter;

    RecyclableBloomFilter(final ObjectFactory<BloomFilter<T>> bloomFilterFactory, final ExpirationFactory expirationFactory) {
        this.recyclableBloomFilter = new AtomicRecyclableReference<>(bloomFilterFactory, expirationFactory);
    }

    @Override
    public boolean mightContain(final T item) {
        return recyclableBloomFilter.reference().mightContain(item);
    }

    @Override
    public boolean add(final T item) {
        return recyclableBloomFilter.reference().add(item);
    }
}
