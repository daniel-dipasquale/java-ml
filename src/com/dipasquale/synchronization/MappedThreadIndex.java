package com.dipasquale.synchronization;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class MappedThreadIndex implements Serializable {
    @Serial
    private static final long serialVersionUID = -9187645626212917120L;
    static final int NOT_MAPPED_INDEX = -1;
    private final Map<Long, Integer> indexes;

    private static void fillIndexes(final Map<Long, Integer> indexes, final List<Long> threadIds) {
        for (int i = 0, index = indexes.size(), c = threadIds.size(); i < c; i++) {
            long threadId = threadIds.get(i);

            if (!indexes.containsKey(threadId)) {
                indexes.put(threadId, index++);
            }
        }
    }

    private static Map<Long, Integer> createIndexes(final List<Long> threadIds) {
        Map<Long, Integer> indexes = new HashMap<>();

        fillIndexes(indexes, threadIds);

        return indexes;
    }

    public MappedThreadIndex(final List<Long> threadIds) {
        this(createIndexes(threadIds));
    }

    int getIndex() {
        return indexes.getOrDefault(Thread.currentThread().getId(), NOT_MAPPED_INDEX);
    }

    public boolean isMapped() {
        return getIndex() > NOT_MAPPED_INDEX;
    }

    int size() {
        return indexes.size();
    }

    public MappedThreadIndex extend(final List<Long> threadIds) {
        Map<Long, Integer> indexes = new HashMap<>();

        fillIndexes(indexes, new ArrayList<>(indexes.keySet()));
        fillIndexes(indexes, threadIds);

        return new MappedThreadIndex(indexes);
    }
}
