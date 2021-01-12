//package com.experimental.data.structure.set.simple;
//
//import com.experimental.data.structure.queue.NodeQueueConcurrent;
//import com.google.common.collect.ImmutableList;
//import com.pasqud.concurrent.ConcurrentHandler;
//import com.pasqud.data.structure.set.simple.InsertOrderSet;
//
//import java.util.List;
//import java.util.concurrent.ConcurrentHashMap;
//import java.util.concurrent.locks.Lock;
//import java.util.concurrent.locks.ReadWriteLock;
//import java.util.concurrent.locks.ReentrantReadWriteLock;
//
//public final class InsertOrderSetConcurrent<T> extends InsertOrderSet<T> {
//    private static final ConcurrentHandler CONCURRENT_HANDLER = ConcurrentHandler.getInstance();
//    private final ReadWriteLock queueLock;
//
//    public InsertOrderSetConcurrent(final boolean shouldPrioritizeWriting) {
//        super(new ConcurrentHashMap<>(), new NodeQueueConcurrent<>(shouldPrioritizeWriting));
//        this.queueLock = new ReentrantReadWriteLock(!shouldPrioritizeWriting);
//    }
//
//    public InsertOrderSetConcurrent() {
//        this(true);
//    }
//
//    public InsertOrderSetConcurrent(final int initialCapacity, final float loadFactor, final int concurrencyLevel, final boolean shouldPrioritizeWriting) {
//        super(new ConcurrentHashMap<>(initialCapacity, loadFactor, concurrencyLevel), new NodeQueueConcurrent<>(shouldPrioritizeWriting));
//        this.queueLock = new ReentrantReadWriteLock(!shouldPrioritizeWriting);
//    }
//
//    public InsertOrderSetConcurrent(final int initialCapacity, final float loadFactor, final int concurrencyLevel) {
//        this(initialCapacity, loadFactor, concurrencyLevel, true);
//    }
//
//    @Override
//    public T first() {
//        return CONCURRENT_HANDLER.get(queueLock, super::first);
//    }
//
//    @Override
//    public T last() {
//        return CONCURRENT_HANDLER.get(queueLock, super::last);
//    }
//
//    @Override
//    public boolean remove(final Object value) {
//        List<Lock> locks = ImmutableList.of(queueLock.readLock());
//
//        return CONCURRENT_HANDLER.invoke(locks, super::remove, value);
//    }
//
//    @Override
//    public void clear() {
//        List<Lock> locks = ImmutableList.of(queueLock.writeLock());
//
//        CONCURRENT_HANDLER.invoke(locks, super::clear);
//    }
//}
