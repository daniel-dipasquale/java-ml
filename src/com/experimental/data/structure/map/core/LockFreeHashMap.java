//package com.experimental.data.structure.map.core;
//
//import com.dipasquale.common.ArgumentValidator;
//import com.dipasquale.common.factory.ObjectFactory;
//import com.dipasquale.data.structure.map.MapBase;
//import com.dipasquale.data.structure.probabilistic.MultiFunctionHashing;
//import com.google.common.collect.ImmutableList;
//import lombok.EqualsAndHashCode;
//import lombok.Getter;
//import lombok.RequiredArgsConstructor;
//
//import java.util.Iterator;
//import java.util.List;
//import java.util.UUID;
//import java.util.concurrent.atomic.AtomicIntegerArray;
//import java.util.concurrent.atomic.AtomicReference;
//import java.util.concurrent.atomic.AtomicReferenceArray;
//
//public final class LockFreeHashMap<TKey, TValue> extends MapBase<TKey, TValue> {
//    private static final int ITEMS_PER_ENTRY = 2;
//    private static final int INITIAL_ENTRY_CAPACITY = (int) Math.pow(2D, 7D);
//    private final int initialCapacity;
//    private final List<MultiFunctionHashing> multiFunctionHashings;
//    private final int concurrencyLevel;
//    private final AtomicReference<AtomicReferenceArray<Object>> dataCas;
//    private final AtomicReference<AtomicIntegerArray> sizeCas;
//
//    private static int calculateMaximumHashFunctions(final long initialCapacity, final long maximumCapacity) { // TODO: figure out the math for this
//        int tiers = 0;
//        long capacity = 0L;
//        double exponent = (int) Math.ceil(Math.log(initialCapacity) / Math.log(2D));
//
//        while (capacity < maximumCapacity) {
//            capacity += (int) Math.pow(2D, 2D * (double) (tiers++) + exponent);
//        }
//
//        return tiers - 1;
//    }
//
//    public LockFreeHashMap(final int maximumCapacity, final int concurrencyLevel) {
//        int initialCapacity = INITIAL_ENTRY_CAPACITY * ITEMS_PER_ENTRY;
//
//        ArgumentValidator.getInstance().ensureGreaterThanOrEqualTo(maximumCapacity, initialCapacity, "maximumCapacity");
//
//        int maximumHashFunctions = calculateMaximumHashFunctions(initialCapacity, maximumCapacity);
//
//        List<MultiFunctionHashing> multiFunctionHashings = ImmutableList.<MultiFunctionHashing>builder()
//                .add(MultiFunctionHashing.createMurmur3_128(maximumHashFunctions, UUID.randomUUID().toString()))
//                .add(MultiFunctionHashing.createSha512(maximumHashFunctions, UUID.randomUUID().toString()))
//                .add(MultiFunctionHashing.createSipHash24(maximumHashFunctions, UUID.randomUUID().toString()))
//                .build();
//
//        this.initialCapacity = initialCapacity;
//        this.multiFunctionHashings = multiFunctionHashings;
//        this.concurrencyLevel = concurrencyLevel;
//        this.dataCas = new AtomicReference<>(new AtomicReferenceArray<>(initialCapacity));
//        this.sizeCas = new AtomicReference<>(new AtomicIntegerArray(concurrencyLevel));
//    }
//
//    public LockFreeHashMap() {
//        this(Integer.MAX_VALUE, 4);
//    }
//
//    @Override
//    public int size() {
//        AtomicIntegerArray size = sizeCas.get();
//        int output = 0;
//
//        for (int i = 0, c = size.length(); i < c; i++) {
//            output += size.get(i);
//        }
//
//        return output;
//    }
//
//    private static int hashCode(final int hashCode, final MultiFunctionHashing multiFunctionHashing, final int hashFunction) {
//        return Long.hashCode(multiFunctionHashing.hashCode(hashCode, hashFunction));
//    }
//
//    private static int hashCode(final long hashCode, final MultiFunctionHashing multiFunctionHashing, final int hashFunction) {
//        return hashCode(Long.hashCode(hashCode), multiFunctionHashing, hashFunction);
//    }
//
//    private static int getIndex(final int hashCode, final MultiFunctionHashing multiFunctionHashing, final int hashFunction, final int length) {
//        return Math.abs(hashCode(hashCode, multiFunctionHashing, hashFunction)) & (length - 1);
//    }
//
//    private static int getIndex(final long hashCode, final MultiFunctionHashing multiFunctionHashing, final int hashFunction, final int length) {
//        return Math.abs(hashCode(hashCode, multiFunctionHashing, hashFunction)) & (length - 1);
//    }
//
//    private static int getKeyIndex(final int hashCode, final MultiFunctionHashing multiFunctionHashing, final int hashFunction, final AtomicReferenceArray<Object> data) {
//        int index = getIndex(hashCode, multiFunctionHashing, hashFunction, data.length() - 2);
//
//        return index - index % ITEMS_PER_ENTRY;
//    }
//
//    private static int getNextDataIndex(final AtomicReferenceArray<Object> data, final int keyHashCode, final MultiFunctionHashing multiFunctionHashing, final int hashFunction) {
//        int index = getIndex(keyHashCode, multiFunctionHashing, hashFunction, 2);
//
//        return data.length() - 1 - index;
//    }
//
//    private static AtomicReferenceArray<Object> getNextDataArray(final AtomicReferenceArray<Object> data, final int keyHashCode, final MultiFunctionHashing multiFunctionHashing, final int hashFunction) {
//        int index = getNextDataIndex(data, keyHashCode, multiFunctionHashing, hashFunction);
//
//        return (AtomicReferenceArray<Object>) data.get(index);
//    }
//
//    private ImmutableKeyValue<TKey, TValue> findEntry(final TKey key, final int keyHashCode, final AtomicReferenceArray<Object> data) {
//        for (MultiFunctionHashing multiFunctionHashing : multiFunctionHashings) {
//            int hashFunction = 0;
//            AtomicReferenceArray<Object> currentData = data;
//
//            while (hashFunction < multiFunctionHashing.getMaximumHashFunctions() && currentData != null) {
//                int index = getKeyIndex(keyHashCode, multiFunctionHashing, hashFunction, currentData);
//                ImmutableKeyValue<TKey, TValue> entry = (ImmutableKeyValue<TKey, TValue>) currentData.get(index);
//
//                if (entry != null && entry.getKey().equals(key)) {
//                    return entry;
//                }
//
//                currentData = getNextDataArray(data, keyHashCode, multiFunctionHashing, hashFunction++);
//            }
//        }
//
//        return null;
//    }
//
//    private ImmutableKeyValue<TKey, TValue> findEntry(final Object key, final int keyHashCode, final AtomicReference<AtomicReferenceArray<Object>> data) {
//        return findEntry((TKey) key, keyHashCode, data.get());
//    }
//
//    private static void ensureKeyIsValid(final Object key) {
//        if (key == null) {
//            throw new IllegalArgumentException("key must not be null");
//        }
//    }
//
//    @Override
//    public boolean containsKey(final Object key) {
//        ensureKeyIsValid(key);
//
//        return findEntry(key, key.hashCode(), dataCas) != null;
//    }
//
//    @Override
//    public TValue get(final Object key) {
//        ensureKeyIsValid(key);
//
//        Entry<TKey, TValue> entry = findEntry(key, key.hashCode(), dataCas);
//
//        if (entry == null) {
//            return null;
//        }
//
//        return entry.getValue();
//    }
//
//    private PutChange<ImmutableKeyValue<TKey, TValue>> addOrUpdateIfNotFoundOrEquals(final AtomicReferenceArray<Object> data, final int index, final ObjectPredicate<ImmutableKeyValue<TKey, TValue>> entryPredicate, final ObjectFactory<ImmutableKeyValue<TKey, TValue>> entryFactory) {
//        ImmutableKeyValue<TKey, TValue> entry = (ImmutableKeyValue<TKey, TValue>) data.get(index);
//
//        if ((entry == null || entryPredicate.test(entry)) && data.weakCompareAndSetVolatile(index + 1, entry, entryFactory)) {
//            ImmutableKeyValue<TKey, TValue> entryNew = entryFactory.create();
//
//            data.set(index, entryNew);
//            data.set(index + 1, entryNew);
//
//            return new PutChange<ImmutableKeyValue<TKey, TValue>>(entryNew, entry == null ? null : entry.getValue(), entry == null);
//        }
//
//        if (entry == null || entryPredicate.test(entry)) {
//            ImmutableKeyValue<TKey, TValue> entryNew;
//
//            do {
//                entryNew = (ImmutableKeyValue<TKey, TValue>) data.get(index);
//            } while (entryNew == entry);
//
//            return new PutChange<ImmutableKeyValue<TKey, TValue>>(entryNew, entry == null ? null : entry.getValue(), entry == null);
//        }
//
//        return null;
//    }
//
//    private AtomicReferenceArray<Object> addIfNull(final AtomicReferenceArray<Object> data, final int index, final ObjectFactory<AtomicReferenceArray<Object>> dataFactory) {
//        AtomicReferenceArray<Object> nextData = (AtomicReferenceArray<Object>) data.get(index);
//
//        if (nextData == null && data.weakCompareAndSetAcquire(index, null, dataFactory)) {
//            AtomicReferenceArray<Object> nextDataNew = dataFactory.create();
//
//            data.set(index, nextDataNew);
//
//            return nextDataNew;
//        }
//
//        if (nextData == null) {
//            AtomicReferenceArray<Object> nextDataNew;
//
//            do {
//                nextDataNew = (AtomicReferenceArray<Object>) data.get(index);
//            } while (nextDataNew == null);
//
//            return nextDataNew;
//        }
//
//        return nextData;
//    }
//
//    private void addSize(final int delta) {
//        AtomicIntegerArray size = sizeCas.get();
//        long threadId = Thread.currentThread().getId();
//        MultiFunctionHashing multiFunctionHashing = multiFunctionHashings.get(0);
//        int hashFunction = hashCode(threadId, multiFunctionHashing, multiFunctionHashing.getMaximumHashFunctions() - 1) & size.length();
//        int index = getIndex(threadId, multiFunctionHashing, hashFunction, size.length());
//
//        size.getAndAdd(index, delta);
//    }
//
//    private PutChange<ImmutableKeyValue<TKey, TValue>> putEntry(final TKey key, final int keyHashCode, final TValue value, final AtomicReferenceArray<Object> data) {
//        for (MultiFunctionHashing multiFunctionHashing : multiFunctionHashings) {
//            int hashFunction = 0;
//            AtomicReferenceArray<Object> currentData = data;
//
//            while (hashFunction < multiFunctionHashing.getMaximumHashFunctions() && currentData != null) {
//                int index = getKeyIndex(keyHashCode, multiFunctionHashing, hashFunction, currentData);
//                PutChange<ImmutableKeyValue<TKey, TValue>> putChange = addOrUpdateIfNotFoundOrEquals(currentData, index, e -> e.getKey().equals(key), () -> new ImmutableKeyValue<>(key, value));
//
//                if (putChange != null) {
//                    addSize(1);
//
//                    return putChange;
//                }
//
//                int nextDataIndex = getNextDataIndex(currentData, keyHashCode, multiFunctionHashing, hashFunction++);
//
//                currentData = addIfNull(currentData, nextDataIndex, new NextDataFactory(currentData));
//            }
//        }
//
//        throw new IllegalArgumentException("maximum capacity has been reached, entry cannot be added");
//    }
//
//    private PutChange<ImmutableKeyValue<TKey, TValue>> putEntry(final TKey key, final int keyHashCode, final TValue value) {
//        return putEntry(key, keyHashCode, value, dataCas.get());
//    }
//
//    @Override
//    protected PutChange<? extends Entry<TKey, TValue>> putEntry(final TKey key, final TValue value) {
//        ensureKeyIsValid(key);
//
//        return putEntry(key, key.hashCode(), value);
//    }
//
//    private ImmutableKeyValue<TKey, TValue> removeIfEquals(final AtomicReferenceArray<Object> data, final int index, final ObjectPredicate<ImmutableKeyValue<TKey, TValue>> predicate) {
//        ImmutableKeyValue<TKey, TValue> entry = (ImmutableKeyValue<TKey, TValue>) data.get(index);
//
//        if (entry != null && predicate.test(entry) && data.weakCompareAndSetVolatile(index + 1, entry, new Object())) {
//            data.set(index, null);
//            data.set(index + 1, null);
//
//            return entry;
//        }
//
//        return null;
//    }
//
//    private ImmutableKeyValue<TKey, TValue> removeEntry(final TKey key, final int keyHashCode, final AtomicReferenceArray<Object> data) {
//        for (MultiFunctionHashing multiFunctionHashing : multiFunctionHashings) {
//            int hashFunction = 0;
//            AtomicReferenceArray<Object> currentData = data;
//
//            while (hashFunction < multiFunctionHashing.getMaximumHashFunctions() && currentData != null) {
//                int index = getKeyIndex(keyHashCode, multiFunctionHashing, hashFunction, currentData);
//                ImmutableKeyValue<TKey, TValue> entry = removeIfEquals(currentData, index, e -> e.getKey().equals(key));
//
//                if (entry != null) {
//                    addSize(-1);
//
//                    return entry;
//                }
//
//                currentData = getNextDataArray(currentData, keyHashCode, multiFunctionHashing, hashFunction++);
//            }
//        }
//
//        return null;
//    }
//
//    private ImmutableKeyValue<TKey, TValue> removeEntry(final TKey key, final int keyHashCode) {
//        return removeEntry(key, keyHashCode, dataCas.get());
//    }
//
//    @Override
//    protected Entry<TKey, TValue> removeEntry(final TKey key) {
//        ensureKeyIsValid(key);
//
//        return removeEntry(key, key.hashCode());
//    }
//
//    @Override
//    public void clear() {
//        sizeCas.set(new AtomicIntegerArray(concurrencyLevel));
//        dataCas.set(new AtomicReferenceArray<>(initialCapacity));
//    }
//
//    @Override
//    protected Iterator<Entry<TKey, TValue>> iterator() {
//        return ImmutableList.<Entry<TKey, TValue>>of().iterator(); // TODO: fix this
//    }
//
//    @FunctionalInterface
//    private interface ObjectPredicate<T> {
//        boolean test(T object);
//    }
//
//    @RequiredArgsConstructor
//    @Getter
//    @EqualsAndHashCode(onlyExplicitlyIncluded = true)
//    private static final class ImmutableKeyValue<TKey, TValue> implements Entry<TKey, TValue> {
//        @EqualsAndHashCode.Include
//        private final TKey key;
//        private final TValue value;
//
//        @Override
//        public TValue setValue(final TValue value) {
//            throw new UnsupportedOperationException();
//        }
//    }
//
//    @RequiredArgsConstructor
//    private static final class NextDataFactory implements ObjectFactory<AtomicReferenceArray<Object>> {
//        private final AtomicReferenceArray<Object> data;
//
//        @Override
//        public AtomicReferenceArray<Object> create() {
//            return new AtomicReferenceArray<>(data.length() * 2);
//        }
//    }
//}
