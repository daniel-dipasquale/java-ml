package com.dipasquale.common.concurrent;

import com.dipasquale.common.factory.ObjectFactory;
import com.dipasquale.common.time.ExpirationFactory;
import com.dipasquale.common.time.ExpirationRecord;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

public final class AtomicRecyclableReference<T> implements Serializable {
    @Serial
    private static final long serialVersionUID = -6186153219728688627L;
    private final RecyclableReference.Factory<T> referenceFactory;
    private final ExpirationFactory expirationFactory;
    private final AtomicReference<RecyclableReferenceContainer<T>> recyclableReferenceContainerProvider;
    private final AtomicLong recycledDateTime;
    private final Queue<RecyclableReference<T>> recycledReferences;
    private final RecyclableReference.Collector<T> recycledReferenceCollector;
    private volatile long recycledConfirmationDateTime;

    private static <T> RecyclableReference.Collector<T> ensureRecycledReferenceCollector(final boolean collectRecycledReferences, final RecyclableReference.Collector<T> recycledReferenceCollector, final Queue<RecyclableReference<T>> recycledReferences) {
        if (!collectRecycledReferences) {
            return recycledReferenceCollector;
        }

        return (RecyclableReference.Collector<T> & Serializable) recycledReferences::add;
    }

    private AtomicRecyclableReference(final RecyclableReference.Factory<T> referenceFactory, final ExpirationFactory expirationFactory, final boolean collectRecycledReferences, final RecyclableReference.Collector<T> recycledReferenceCollector) {
        Queue<RecyclableReference<T>> recycledReferences = new ConcurrentLinkedQueue<>();

        this.referenceFactory = referenceFactory;
        this.expirationFactory = expirationFactory;
        this.recyclableReferenceContainerProvider = new AtomicReference<>();
        this.recycledDateTime = new AtomicLong(Long.MIN_VALUE);
        this.recycledReferences = recycledReferences;
        this.recycledReferenceCollector = ensureRecycledReferenceCollector(collectRecycledReferences, recycledReferenceCollector, recycledReferences);
        this.recycledConfirmationDateTime = Long.MIN_VALUE;
    }

    public AtomicRecyclableReference(final RecyclableReference.Factory<T> referenceFactory, final ExpirationFactory expirationFactory, final RecyclableReference.Collector<T> recycledReferenceCollector) {
        this(referenceFactory, expirationFactory, false, recycledReferenceCollector);
    }

    public AtomicRecyclableReference(final RecyclableReference.Factory<T> referenceFactory, final ExpirationFactory expirationFactory, final boolean collectRecycledReferences) {
        this(referenceFactory, expirationFactory, collectRecycledReferences, null);
    }

    public AtomicRecyclableReference(final RecyclableReference.Factory<T> referenceFactory, final ExpirationFactory expirationFactory) {
        this(referenceFactory, expirationFactory, false);
    }

    public AtomicRecyclableReference(final ObjectFactory<T> referenceFactory, final ExpirationFactory expirationFactory, final RecyclableReference.Collector<T> recycledReferenceCollector) {
        this(new RecyclableReferenceFactory<>(referenceFactory), expirationFactory, false, recycledReferenceCollector);
    }

    public AtomicRecyclableReference(final ObjectFactory<T> referenceFactory, final ExpirationFactory expirationFactory, final boolean collectRecycledReferences) {
        this(new RecyclableReferenceFactory<>(referenceFactory), expirationFactory, collectRecycledReferences);
    }

    public AtomicRecyclableReference(final ObjectFactory<T> referenceFactory, final ExpirationFactory expirationFactory) {
        this(new RecyclableReferenceFactory<>(referenceFactory), expirationFactory);
    }

    private boolean tryReplaceRecycledDateTime(final ExpirationRecord expirationRecord) {
        boolean[] replaced = new boolean[]{false};

        recycledDateTime.accumulateAndGet(-1L, (oldRecycledDateTime, __) -> {
            if (oldRecycledDateTime != recycledConfirmationDateTime || oldRecycledDateTime >= expirationRecord.getExpirationDateTime()) {
                return oldRecycledDateTime;
            }

            replaced[0] = true;

            return expirationRecord.getExpirationDateTime();
        });

        return replaced[0];
    }

    private RecyclableReferenceContainer<T> getReferenceOrRecycleIfExpired(final RecyclableReferenceContainer<T> recyclableReferenceContainer, final ExpirationRecord expirationRecord) {
        if (recyclableReferenceContainer != null && expirationRecord.getCurrentDateTime() < recyclableReferenceContainer.expirationDateTime) {
            return recyclableReferenceContainer;
        }

        if (!tryReplaceRecycledDateTime(expirationRecord)) {
            RecyclableReferenceContainer<T> recyclableReferenceContainerFixed = recyclableReferenceContainerProvider.get();

            while (recyclableReferenceContainerFixed == recyclableReferenceContainer) {
                Thread.onSpinWait();
                recyclableReferenceContainerFixed = recyclableReferenceContainerProvider.get();
            }

            return recyclableReferenceContainerFixed;
        }

        if (recyclableReferenceContainer != null && recycledReferenceCollector != null) {
            recycledReferenceCollector.collect(recyclableReferenceContainer.get());
        }

        return new RecyclableReferenceContainer<>(referenceFactory, expirationRecord.getExpirationDateTime());
    }

    private RecyclableReferenceContainer<T> getReferenceOrRecycleIfExpired(final RecyclableReferenceContainer<T> recyclableReferenceContainer) {
        return getReferenceOrRecycleIfExpired(recyclableReferenceContainer, expirationFactory.create());
    }

    private RecyclableReferenceContainer<T> getReferenceOrRecycleIfExpired() {
        RecyclableReferenceContainerAudit<T> audit = new RecyclableReferenceContainerAudit<>();

        try {
            return recyclableReferenceContainerProvider.accumulateAndGet(null, (oldRecyclableReferenceContainer, __) -> {
                audit.previous = oldRecyclableReferenceContainer;

                return audit.next = getReferenceOrRecycleIfExpired(oldRecyclableReferenceContainer);
            });
        } finally {
            if (audit.previous != audit.next) {
                recycledConfirmationDateTime = audit.next.expirationDateTime;
            }
        }
    }

    public RecyclableReference<T> recyclableReference() {
        return getReferenceOrRecycleIfExpired().get();
    }

    public T reference() {
        return recyclableReference().getReference();
    }

    public RecyclableReference<T> pollRecycled() {
        return recycledReferences.poll();
    }

    private boolean equals(final AtomicRecyclableReference<T> other) {
        return Objects.equals(reference(), other.reference());
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }

        if (other instanceof AtomicRecyclableReference<?>) {
            return equals((AtomicRecyclableReference<T>) other);
        }

        return false;
    }

    @Override
    public int hashCode() {
        return reference().hashCode();
    }

    @Override
    public String toString() {
        return reference().toString();
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class RecyclableReferenceFactory<T> implements RecyclableReference.Factory<T>, Serializable {
        @Serial
        private static final long serialVersionUID = 5459978221432448395L;
        private final ObjectFactory<T> referenceFactory;

        @Override
        public T create(final long dateTime) {
            return referenceFactory.create();
        }
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class RecyclableReferenceContainer<T> implements Serializable {
        @Serial
        private static final long serialVersionUID = 731224291034674365L;
        private final RecyclableReference.Factory<T> referenceFactory;
        private final long expirationDateTime;
        private final AtomicBoolean initialized = new AtomicBoolean();
        private volatile RecyclableReference<T> recyclableReference = null;

        private RecyclableReference<T> get() {
            RecyclableReference<T> recyclableReferenceFixed;

            if (!initialized.compareAndSet(false, true)) {
                recyclableReferenceFixed = recyclableReference;

                while (recyclableReferenceFixed == null) {
                    Thread.onSpinWait();
                    recyclableReferenceFixed = recyclableReference;
                }
            } else {
                T reference = referenceFactory.create(expirationDateTime);

                recyclableReferenceFixed = new RecyclableReference<>(reference, expirationDateTime);
                recyclableReference = recyclableReferenceFixed;
            }

            return recyclableReferenceFixed;
        }
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class RecyclableReferenceContainerAudit<T> {
        private RecyclableReferenceContainer<T> previous;
        private RecyclableReferenceContainer<T> next;
    }
}