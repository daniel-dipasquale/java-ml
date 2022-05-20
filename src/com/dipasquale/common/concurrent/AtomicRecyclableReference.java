package com.dipasquale.common.concurrent;

import com.dipasquale.common.factory.ObjectFactory;
import com.dipasquale.common.time.ExpirationFactory;
import com.dipasquale.common.time.ExpirationRecord;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

public final class AtomicRecyclableReference<T> implements Serializable {
    @Serial
    private static final long serialVersionUID = -6186153219728688627L;
    private final RecyclableReferenceFactory<T> referenceFactory;
    private final ExpirationFactory expirationFactory;
    private final AtomicReference<AtomicRecyclableReferenceContainer<T>> referenceContainer;
    private final AtomicLong recycledDateTime;
    private final Queue<RecyclableReferenceContainer<T>> recycledReferenceContainers;
    private final RecyclableReferenceCollector<T> recycledReferenceCollector;
    private volatile long recyclingConfirmedDateTime;

    private static <T> RecyclableReferenceCollector<T> ensureRecycledReferenceCollector(final boolean collectRecycledReferences, final RecyclableReferenceCollector<T> recycledReferenceCollector, final Queue<RecyclableReferenceContainer<T>> recycledReferenceContainers) {
        if (!collectRecycledReferences) {
            return recycledReferenceCollector;
        }

        return (RecyclableReferenceCollector<T> & Serializable) (reference, exception, recycledDateTime) -> recycledReferenceContainers.add(new RecyclableReferenceContainer<>(reference, exception, recycledDateTime));
    }

    private AtomicRecyclableReference(final RecyclableReferenceFactory<T> referenceFactory, final ExpirationFactory expirationFactory, final boolean collectRecycledReferences, final RecyclableReferenceCollector<T> recycledReferenceCollector, final Queue<RecyclableReferenceContainer<T>> recycledReferenceContainers) {
        this.referenceFactory = referenceFactory;
        this.expirationFactory = expirationFactory;
        this.referenceContainer = new AtomicReference<>();
        this.recycledDateTime = new AtomicLong(Long.MIN_VALUE);
        this.recycledReferenceContainers = recycledReferenceContainers;
        this.recycledReferenceCollector = ensureRecycledReferenceCollector(collectRecycledReferences, recycledReferenceCollector, recycledReferenceContainers);
        this.recyclingConfirmedDateTime = Long.MIN_VALUE;
    }

    private AtomicRecyclableReference(final RecyclableReferenceFactory<T> referenceFactory, final ExpirationFactory expirationFactory, final boolean collectRecycledReferences, final RecyclableReferenceCollector<T> recycledReferenceCollector) {
        this(referenceFactory, expirationFactory, collectRecycledReferences, recycledReferenceCollector, new ConcurrentLinkedQueue<>());
    }

    public AtomicRecyclableReference(final RecyclableReferenceFactory<T> referenceFactory, final ExpirationFactory expirationFactory, final RecyclableReferenceCollector<T> recycledReferenceCollector) {
        this(referenceFactory, expirationFactory, false, recycledReferenceCollector);
    }

    public AtomicRecyclableReference(final RecyclableReferenceFactory<T> referenceFactory, final ExpirationFactory expirationFactory, final boolean collectRecycledReferences) {
        this(referenceFactory, expirationFactory, collectRecycledReferences, null);
    }

    public AtomicRecyclableReference(final RecyclableReferenceFactory<T> referenceFactory, final ExpirationFactory expirationFactory) {
        this(referenceFactory, expirationFactory, false);
    }

    public AtomicRecyclableReference(final ObjectFactory<T> referenceFactory, final ExpirationFactory expirationFactory, final RecyclableReferenceCollector<T> recycledReferenceCollector) {
        this(new ParameterlessRecyclableReferenceFactory<>(referenceFactory), expirationFactory, false, recycledReferenceCollector);
    }

    public AtomicRecyclableReference(final ObjectFactory<T> referenceFactory, final ExpirationFactory expirationFactory, final boolean collectRecycledReferences) {
        this(new ParameterlessRecyclableReferenceFactory<>(referenceFactory), expirationFactory, collectRecycledReferences);
    }

    public AtomicRecyclableReference(final ObjectFactory<T> referenceFactory, final ExpirationFactory expirationFactory) {
        this(new ParameterlessRecyclableReferenceFactory<>(referenceFactory), expirationFactory);
    }

    private boolean tryReplaceRecycledDateTime(final ExpirationRecord expirationRecord) {
        boolean[] replaced = new boolean[]{false};

        recycledDateTime.accumulateAndGet(-1L, (oldRecycledDateTime, __) -> {
            if (oldRecycledDateTime != recyclingConfirmedDateTime || oldRecycledDateTime >= expirationRecord.getExpirationDateTime()) {
                return oldRecycledDateTime;
            }

            replaced[0] = true;

            return expirationRecord.getExpirationDateTime();
        });

        return replaced[0];
    }

    private AtomicRecyclableReferenceContainer<T> getReferenceOrRecycleIfExpired(final AtomicRecyclableReferenceContainer<T> oldReferenceContainer, final ExpirationRecord expirationRecord) {
        long currentDateTime = expirationRecord.getCurrentDateTime();

        if (oldReferenceContainer != null && currentDateTime < oldReferenceContainer.getExpirationDateTime()) {
            return oldReferenceContainer;
        }

        if (!tryReplaceRecycledDateTime(expirationRecord)) {
            AtomicRecyclableReferenceContainer<T> extractedReferenceContainer = referenceContainer.get();

            while (extractedReferenceContainer == oldReferenceContainer) {
                Thread.onSpinWait();
                extractedReferenceContainer = referenceContainer.get();
            }

            return extractedReferenceContainer;
        }

        if (oldReferenceContainer != null && recycledReferenceCollector != null) {
            RecyclableReferenceContainer<T> recycledReferenceContainer = oldReferenceContainer.get();

            recycledReferenceCollector.collect(recycledReferenceContainer.getReference(), recycledReferenceContainer.getException(), currentDateTime);
        }

        return new AtomicRecyclableReferenceContainer<>(referenceFactory, expirationRecord.getExpirationDateTime());
    }

    private AtomicRecyclableReferenceContainer<T> getReferenceOrRecycleIfExpired() {
        Audit<T> audit = new Audit<>();

        try {
            return referenceContainer.accumulateAndGet(null, (oldReferenceContainer, __) -> {
                audit.previous = oldReferenceContainer;

                return audit.next = getReferenceOrRecycleIfExpired(oldReferenceContainer, expirationFactory.create());
            });
        } finally {
            if (audit.previous != audit.next) {
                recyclingConfirmedDateTime = audit.next.getExpirationDateTime();
            }
        }
    }

    public T getReference() {
        return getReferenceOrRecycleIfExpired().get().resolve();
    }

    public T pollRecycledReference() {
        RecyclableReferenceContainer<T> referenceContainer = recycledReferenceContainers.poll();

        if (referenceContainer == null) {
            return null;
        }

        return referenceContainer.resolve();
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class ParameterlessRecyclableReferenceFactory<T> implements RecyclableReferenceFactory<T>, Serializable {
        @Serial
        private static final long serialVersionUID = 5459978221432448395L;
        private final ObjectFactory<T> referenceFactory;

        @Override
        public T create(final long dateTime) {
            return referenceFactory.create();
        }
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class Audit<T> {
        private AtomicRecyclableReferenceContainer<T> previous = null;
        private AtomicRecyclableReferenceContainer<T> next = null;
    }
}