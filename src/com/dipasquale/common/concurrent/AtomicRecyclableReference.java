package com.dipasquale.common.concurrent;

import com.dipasquale.common.factory.ObjectFactory;
import com.dipasquale.common.time.ExpirationFactory;
import com.dipasquale.common.time.ExpirationRecord;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
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
    private final AtomicReference<AtomicReferenceContainer<T>> referenceContainer;
    private final AtomicLong recycledDateTime;
    private final Queue<ReferenceContainer<T>> recycledReferenceContainers;
    private final RecyclableReferenceCollector<T> recycledReferenceCollector;
    private volatile long recyclingConfirmedDateTime;

    private static <T> RecyclableReferenceCollector<T> ensureRecycledReferenceCollector(final boolean collectRecycledReferences, final RecyclableReferenceCollector<T> recycledReferenceCollector, final Queue<ReferenceContainer<T>> recycledReferenceContainers) {
        if (!collectRecycledReferences) {
            return recycledReferenceCollector;
        }

        return (RecyclableReferenceCollector<T> & Serializable) (reference, exception, recycledDateTime) -> recycledReferenceContainers.add(new ReferenceContainer<>(reference, exception, recycledDateTime));
    }

    private AtomicRecyclableReference(final RecyclableReferenceFactory<T> referenceFactory, final ExpirationFactory expirationFactory, final boolean collectRecycledReferences, final RecyclableReferenceCollector<T> recycledReferenceCollector) {
        Queue<ReferenceContainer<T>> recycledReferenceContainers = new ConcurrentLinkedQueue<>();

        this.referenceFactory = referenceFactory;
        this.expirationFactory = expirationFactory;
        this.referenceContainer = new AtomicReference<>();
        this.recycledDateTime = new AtomicLong(Long.MIN_VALUE);
        this.recycledReferenceContainers = recycledReferenceContainers;
        this.recycledReferenceCollector = ensureRecycledReferenceCollector(collectRecycledReferences, recycledReferenceCollector, recycledReferenceContainers);
        this.recyclingConfirmedDateTime = Long.MIN_VALUE;
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

    private AtomicReferenceContainer<T> getReferenceOrRecycleIfExpired(final AtomicReferenceContainer<T> oldReferenceContainer, final ExpirationRecord expirationRecord) {
        long currentDateTime = expirationRecord.getCurrentDateTime();

        if (oldReferenceContainer != null && currentDateTime < oldReferenceContainer.getExpirationDateTime()) {
            return oldReferenceContainer;
        }

        if (!tryReplaceRecycledDateTime(expirationRecord)) {
            AtomicReferenceContainer<T> fixedReferenceContainer = referenceContainer.get();

            while (fixedReferenceContainer == oldReferenceContainer) {
                Thread.onSpinWait();
                fixedReferenceContainer = referenceContainer.get();
            }

            return fixedReferenceContainer;
        }

        if (oldReferenceContainer != null && recycledReferenceCollector != null) {
            ReferenceContainer<T> recycledReferenceContainer = oldReferenceContainer.getReferenceContainer();

            recycledReferenceCollector.collect(recycledReferenceContainer.getReference(), recycledReferenceContainer.getException(), currentDateTime);
        }

        return new AtomicReferenceContainer<>(referenceFactory, expirationRecord.getExpirationDateTime());
    }

    private AtomicReferenceContainer<T> getReferenceOrRecycleIfExpired() {
        AtomicReferenceContainerAudit<T> audit = new AtomicReferenceContainerAudit<>();

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

    private static <T> T extractReference(final ReferenceContainer<T> referenceContainer) {
        RuntimeException exception = referenceContainer.getException();

        if (exception != null) {
            throw exception;
        }

        return referenceContainer.getReference();
    }

    public T getReference() {
        return extractReference(getReferenceOrRecycleIfExpired().getReferenceContainer());
    }

    public T pollRecycledReference() {
        ReferenceContainer<T> referenceContainer = recycledReferenceContainers.poll();

        if (referenceContainer == null) {
            return null;
        }

        return extractReference(referenceContainer);
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

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class AtomicReferenceContainerAudit<T> {
        private AtomicReferenceContainer<T> previous = null;
        private AtomicReferenceContainer<T> next = null;
    }
}