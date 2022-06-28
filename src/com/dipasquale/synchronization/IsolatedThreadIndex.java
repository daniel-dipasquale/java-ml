package com.dipasquale.synchronization;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class IsolatedThreadIndex implements Serializable {
    @Serial
    private static final long serialVersionUID = -9187645626212917120L;
    static final int NOT_MAPPED_INDEX = -1;
    private final int[] indexes;
    private final long multiplier;
    private final long maximum;
    private final int size;
    @Getter(AccessLevel.PRIVATE)
    private final Set<Long> threadIds;

    private static int calculateHashCode(final long threadId, final long multiplier, final long maximum) {
        long hashCode = (threadId * multiplier) >> ((multiplier / 10L) % 28L);

        return (int) (hashCode % maximum);
    }

    private static int[] createIndexes(final List<Long> threadIds, final long multiplier, final long size) {
        int[] indexes = new int[(int) size];

        for (int i = 0, c = threadIds.size(); i < c; i++) {
            long threadId = threadIds.get(i);
            int hashCode = calculateHashCode(threadId, multiplier, size);

            indexes[hashCode] = i + 1;
        }

        return indexes;
    }

    private static HashParams createHashParams(final Set<Long> threadIds) {
        List<Long> fixedThreadIds = new ArrayList<>(threadIds);
        int size = threadIds.size();
        long maximum = (long) size * size;
        Set<Integer> hashCodes = new HashSet<>();

        for (long multiplier = 31L, maximumMultiplier = Long.MAX_VALUE / size; multiplier < maximumMultiplier; multiplier++) {
            boolean zeroCollisions = true;

            for (int i = 0; i < size && zeroCollisions; i++) {
                long threadId = fixedThreadIds.get(i);
                int hashCode = calculateHashCode(threadId, multiplier, maximum);

                zeroCollisions = hashCodes.add(hashCode);
            }

            if (zeroCollisions) {
                int[] indexes = createIndexes(fixedThreadIds, multiplier, maximum);

                return new HashParams(multiplier, maximum, indexes);
            }

            hashCodes.clear();
        }

        Iterable<? extends CharSequence> threadIdsIterable = threadIds.stream()
                .map(threadId -> (CharSequence) threadId.toString())
                ::iterator;

        String message = String.format("unable to find a hash for thread ids: %s", String.join(", ", threadIdsIterable));

        throw new IllegalArgumentException(message);
    }

    private IsolatedThreadIndex(final HashParams hashParams, final Set<Long> threadIds) {
        this.indexes = hashParams.indexes;
        this.multiplier = hashParams.multiplier;
        this.maximum = hashParams.maximum;
        this.size = threadIds.size();
        this.threadIds = Set.copyOf(threadIds);
    }

    public IsolatedThreadIndex(final Set<Long> threadIds) {
        this(createHashParams(threadIds), threadIds);
    }

    public int getCurrentIndex() {
        long threadId = Thread.currentThread().getId();
        int hashCode = calculateHashCode(threadId, multiplier, maximum);

        return indexes[hashCode] - 1;
    }

    public boolean isCurrentMapped() {
        return getCurrentIndex() > NOT_MAPPED_INDEX;
    }

    public int size() {
        return size;
    }

    public IsolatedThreadIndex extend(final Set<Long> threadIds) {
        Set<Long> extendedThreadIds = new HashSet<>();

        extendedThreadIds.addAll(getThreadIds());
        extendedThreadIds.addAll(threadIds);

        return new IsolatedThreadIndex(extendedThreadIds);
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class HashParams {
        private final long multiplier;
        private final long maximum;
        private final int[] indexes;
    }
}
