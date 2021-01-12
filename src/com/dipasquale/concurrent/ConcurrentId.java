package com.dipasquale.concurrent;

import com.google.common.base.Objects;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class ConcurrentId implements Comparable<ConcurrentId> {
    private final Comparable<Object> majorId;
    private final Comparable<Object> minorId;
    private final Comparable<Object> revisionId;

    public static <T extends Comparable<T>> ConcurrentId create(final T majorId, final T minorId, final T revisionId) {
        Comparable<Object> majorIdObject = (Comparable<Object>) (Object) majorId;
        Comparable<Object> minorIdObject = (Comparable<Object>) (Object) minorId;
        Comparable<Object> revisionIdObject = (Comparable<Object>) (Object) revisionId;

        return new ConcurrentId(majorIdObject, minorIdObject, revisionIdObject);
    }

    @Override
    public int compareTo(final ConcurrentId concurrentId) {
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
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }

        if (obj instanceof ConcurrentId) {
            return compareTo((ConcurrentId) obj) == 0;
        }

        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(majorId, minorId, revisionId);
    }

    @Override
    public String toString() {
        return String.format("%s.%s.%s", majorId, minorId, revisionId);
    }
}
