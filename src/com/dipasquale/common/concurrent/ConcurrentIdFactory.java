package com.dipasquale.common.concurrent;

import com.dipasquale.common.IdFactory;
import com.dipasquale.common.ObjectFactory;
import com.dipasquale.common.time.ExpirationFactory;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public final class ConcurrentIdFactory implements IdFactory<ConcurrentId<Long>> {
    private final AtomicRecyclableReference<MajorId> recyclableMajorId;
    private final IdFactory<Long> minorIdFactory;

    public ConcurrentIdFactory(final ExpirationFactory expirationFactory, final ObjectFactory<ConcurrentHashMap<Long, MinorId>> minorIdContainerFactory, final IdFactory<Long> minorIdFactory) {
        this.recyclableMajorId = new AtomicRecyclableReference<>(createMajorIdFactory(minorIdContainerFactory), expirationFactory);
        this.minorIdFactory = minorIdFactory;
    }

    public ConcurrentIdFactory(final ExpirationFactory expirationFactory, final ObjectFactory<ConcurrentHashMap<Long, MinorId>> minorIdContainerFactory) {
        this(expirationFactory, minorIdContainerFactory, () -> Thread.currentThread().getId());
    }

    private static RecyclableReference.Factory<MajorId> createMajorIdFactory(final ObjectFactory<ConcurrentHashMap<Long, MinorId>> minorIdContainerFactory) {
        return edt -> new MajorId(edt, minorIdContainerFactory.create());
    }

    @Override
    public ConcurrentId<Long> createId() {
        MajorId majorId = recyclableMajorId.reference();
        MinorId minorId = majorId.minorIds.computeIfAbsent(minorIdFactory.createId(), MinorId::new);

        return new ConcurrentId<>(majorId.majorId, minorId.minorId, minorId.revisionId.getAndIncrement());
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    static final class MinorId {
        private final long minorId;
        private final AtomicLong revisionId = new AtomicLong();
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class MajorId {
        private final long majorId;
        private final Map<Long, MinorId> minorIds;
    }
}
