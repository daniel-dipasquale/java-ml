package com.dipasquale.concurrent;

import com.dipasquale.common.ExpirySupport;
import com.dipasquale.common.IdFactory;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public final class ConcurrentIdFactory implements IdFactory<ConcurrentId> {
    private final AtomicRecyclableReference<MajorIdContainer> recyclableMajorIdContainer;
    private final IdFactory<Long> minorIdFactory;

    public ConcurrentIdFactory(final ExpirySupport expirySupport, final IdFactory<Long> minorIdFactory, final int initialCapacity, final float loadFactor, final int concurrencyLevel) {
        this.recyclableMajorIdContainer = new AtomicRecyclableReference<>(createMajorIdContainerFactory(initialCapacity, loadFactor, concurrencyLevel), expirySupport);
        this.minorIdFactory = minorIdFactory;
    }

    public ConcurrentIdFactory(final ExpirySupport expirySupport, final int initialCapacity, final float loadFactor, final int concurrencyLevel) {
        this(expirySupport, IdFactory.createThreadIdFactory(), initialCapacity, loadFactor, concurrencyLevel);
    }

    private static RecyclableReference.Factory<MajorIdContainer> createMajorIdContainerFactory(final int initialCapacity, final float loadFactor, final int concurrencyLevel) {
        return edt -> new MajorIdContainer(edt, new ConcurrentHashMap<>(initialCapacity, loadFactor, concurrencyLevel));
    }

    @Override
    public ConcurrentId createId() {
        MajorIdContainer majorIdContainer = recyclableMajorIdContainer.reference();
        MinorIdContainer minorIdContainer = majorIdContainer.minorIdContainers.computeIfAbsent(minorIdFactory.createId(), MinorIdContainer::new);

        return ConcurrentId.create(majorIdContainer.majorId, minorIdContainer.minorId, minorIdContainer.revisionId.getAndIncrement());
    }

    @RequiredArgsConstructor
    private static final class MinorIdContainer {
        private final long minorId;
        private final AtomicLong revisionId = new AtomicLong(0L);
    }

    @RequiredArgsConstructor
    private static final class MajorIdContainer {
        private final long majorId;
        private final Map<Long, MinorIdContainer> minorIdContainers;
    }
}
