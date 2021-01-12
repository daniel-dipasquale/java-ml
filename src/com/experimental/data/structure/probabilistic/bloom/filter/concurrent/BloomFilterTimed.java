package com.experimental.data.structure.probabilistic.bloom.filter.concurrent;

import com.dipasquale.common.ExpiryRecord;
import com.dipasquale.common.ExpirySupport;
import com.dipasquale.data.structure.probabilistic.MultiFunctionHashing;
import com.dipasquale.data.structure.probabilistic.bloom.filter.BloomFilter;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicLongArray;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
final class BloomFilterTimed<T> implements BloomFilter<T> {
    private final MultiFunctionHashing multiFunctionHashing;
    private final AtomicLongArray expiryDateTimes;
    private final int hashFunctions;
    private final ExpirySupport expirySupport;

    static <T> BloomFilterTimed<T> create(final MultiFunctionHashing multiFunctionHashing, final int size, final int hashFunctions, final ExpirySupport expirySupport) {
        AtomicLongArray expiryDateTimes = new AtomicLongArray(size);

        return new BloomFilterTimed<>(multiFunctionHashing, expiryDateTimes, hashFunctions, expirySupport);
    }

    private long[] selectOrUpdateExpiryDateTimes(final T item, final ExpiryDateTimeRetriever retriever) {
        long[] output = new long[hashFunctions];
        int hashCode = item.hashCode();

        for (int i = 0; i < hashFunctions; i++) {
            long hashCodeMerged = Math.abs(multiFunctionHashing.hashCode(hashCode, i));
            int index = (int) (hashCodeMerged % (long) expiryDateTimes.length());

            output[i] = retriever.get(index);
        }

        return output;
    }

    @Override
    public boolean mightContain(final T item) {
        ExpiryRecord expiryRecord = expirySupport.next();
        long[] expiryDateTimesOld = selectOrUpdateExpiryDateTimes(item, expiryDateTimes::get);

        long expiryDateTime = Arrays.stream(expiryDateTimesOld).min()
                .orElse(0L);

        return !expiryRecord.isExpired(expiryDateTime);
    }

    @Override
    public boolean add(final T item) {
        ExpiryRecord record = expirySupport.next();
        long[] expiryDateTimesOld = selectOrUpdateExpiryDateTimes(item, i -> expiryDateTimes.getAndAccumulate(i, record.getExpiryDateTime(), Math::max));
        boolean[] added = new boolean[]{false};

        Arrays.stream(expiryDateTimesOld)
                .filter(record::isExpired)
                .findFirst()
                .ifPresent(edt -> added[0] = true);

        return added[0];
    }

    @FunctionalInterface
    private interface ExpiryDateTimeRetriever {
        long get(int index);
    }
}
