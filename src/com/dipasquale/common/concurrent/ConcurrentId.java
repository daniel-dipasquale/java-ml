/*
 * java-ml
 * (c) 2021 daniel-dipasquale
 * released under the MIT license
 */

package com.dipasquale.common.concurrent;

import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@RequiredArgsConstructor
public final class ConcurrentId<T extends Comparable<T>> implements Comparable<ConcurrentId<T>>, Serializable {
    @Serial
    private static final long serialVersionUID = 2874790523023759695L;
    private final T majorId;
    private final T minorId;
    private final T revisionId;

    @Override
    public int compareTo(final ConcurrentId<T> concurrentId) {
        int comparison = majorId.compareTo(concurrentId.majorId);

        if (comparison != 0) {
            return comparison;
        }

        comparison = minorId.compareTo(concurrentId.minorId);

        if (comparison != 0) {
            return comparison;
        }

        return revisionId.compareTo(concurrentId.revisionId);
    }

    @Override
    public String toString() {
        return String.format("%s.%s.%s", majorId, minorId, revisionId);
    }
}
