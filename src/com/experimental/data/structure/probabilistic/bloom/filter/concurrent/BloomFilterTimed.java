package com.experimental.data.structure.probabilistic.bloom.filter.concurrent;

import com.dipasquale.common.time.ExpirationFactory;
import com.dipasquale.common.time.ExpirationRecord;
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
    private final ExpirationFactory expirationFactory;

    static <T> BloomFilterTimed<T> create(final MultiFunctionHashing multiFunctionHashing, final int size, final int hashFunctions, final ExpirationFactory expirationFactory) {
        AtomicLongArray expiryDateTimes = new AtomicLongArray(size);

        return new BloomFilterTimed<>(multiFunctionHashing, expiryDateTimes, hashFunctions, expirationFactory);
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
        ExpirationRecord expirationRecord = expirationFactory.create();
        long[] expiryDateTimesOld = selectOrUpdateExpiryDateTimes(item, expiryDateTimes::get);

        long expiryDateTime = Arrays.stream(expiryDateTimesOld).min()
                .orElse(Long.MIN_VALUE);

        return expirationRecord.getCurrentDateTime() < expiryDateTime;
    }

    @Override
    public boolean add(final T item) {
        ExpirationRecord expirationRecord = expirationFactory.create();
        long[] expiryDateTimesOld = selectOrUpdateExpiryDateTimes(item, i -> expiryDateTimes.getAndAccumulate(i, expirationRecord.getExpirationDateTime(), Math::max));
        boolean[] added = new boolean[1];

        Arrays.stream(expiryDateTimesOld)
                .filter(edt -> expirationRecord.getExpirationDateTime() >= edt)
                .findFirst()
                .ifPresent(edt -> added[0] = true);

        return added[0];
    }

    @FunctionalInterface
    private interface ExpiryDateTimeRetriever {
        long get(int index);
    }
}
