/*
 * java-ml
 * (c) 2021 daniel-dipasquale
 * released under the MIT license
 */

package com.dipasquale.data.structure.probabilistic.count.min.sketch.concurrent;

import com.dipasquale.data.structure.probabilistic.count.min.sketch.CountMinSketch;

@FunctionalInterface
public interface RecycledCountMinSketchCollector<T> {
    void collect(CountMinSketch<T> countMinSketch, long recycledDateTime);
}
