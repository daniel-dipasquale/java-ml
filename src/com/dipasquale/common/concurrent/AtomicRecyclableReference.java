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
    private final AtomicReference<RecyclableReferenceEnvelope<T>> recyclableReferenceEnvelope;
    private final AtomicLong recycledDateTime;
    private final Queue<RecyclableReference<T>> recycledReferences;
    private final RecyclableReference.Collector<T> recycledReferenceCollector;
    private volatile long recycledConfirmationDateTime;

    private AtomicRecyclableReference(final RecyclableReference.Factory<T> referenceFactory, final ExpirationFactory expirationFactory, final boolean collectRecycledReferences, final RecyclableReference.Collector<T> recycledReferenceCollector) {
        Queue<RecyclableReference<T>> recycledReferences = new ConcurrentLinkedQueue<>();

        RecyclableReference.Collector<T> recycledReferenceCollectorFixed = !collectRecycledReferences
                ? recycledReferenceCollector
                : (RecyclableReference.Collector<T> & Serializable) recycledReferences::add;

        this.referenceFactory = referenceFactory;
        this.expirationFactory = expirationFactory;
        this.recyclableReferenceEnvelope = new AtomicReference<>();
        this.recycledDateTime = new AtomicLong(Long.MIN_VALUE);
        this.recycledReferences = recycledReferences;
        this.recycledReferenceCollector = recycledReferenceCollectorFixed;
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
        boolean[] replaced = new boolean[1];

        recycledDateTime.accumulateAndGet(-1L, (ordt, nrdt) -> {
            if (ordt != recycledConfirmationDateTime || ordt >= expirationRecord.getExpirationDateTime()) {
                return ordt;
            }

            replaced[0] = true;

            return expirationRecord.getExpirationDateTime();
        });

        return replaced[0];
    }

    private RecyclableReferenceEnvelope<T> recycleIfExpired(final RecyclableReferenceEnvelope<T> referenceEnvelope, final ExpirationRecord expirationRecord) {
        if (referenceEnvelope != null && expirationRecord.getCurrentDateTime() < referenceEnvelope.expirationDateTime) {
            return referenceEnvelope;
        }

        if (!tryReplaceRecycledDateTime(expirationRecord)) {
            RecyclableReferenceEnvelope<T> recyclableReferenceEnvelopeFixed = recyclableReferenceEnvelope.get();

            while (recyclableReferenceEnvelopeFixed == referenceEnvelope) {
                Thread.onSpinWait();
                recyclableReferenceEnvelopeFixed = recyclableReferenceEnvelope.get();
            }

            return recyclableReferenceEnvelopeFixed;
        }

        if (referenceEnvelope != null && recycledReferenceCollector != null) {
            recycledReferenceCollector.collect(referenceEnvelope.retrieve());
        }

        return new RecyclableReferenceEnvelope<>(referenceFactory, expirationRecord.getExpirationDateTime());
    }

    private RecyclableReferenceEnvelope<T> recycleIfExpired(final RecyclableReferenceEnvelope<T> recyclableReferenceEnvelope) {
        return recycleIfExpired(recyclableReferenceEnvelope, expirationFactory.create());
    }

    private RecyclableReferenceEnvelope<T> compareAndSwapIfExpired() {
        RecyclableReferenceEnvelopeAudit<T> audit = new RecyclableReferenceEnvelopeAudit<>();

        try {
            return recyclableReferenceEnvelope.accumulateAndGet(null, (orre, nrre) -> {
                audit.previous = orre;

                return audit.next = recycleIfExpired(orre);
            });
        } finally {
            if (audit.previous != audit.next) {
                recycledConfirmationDateTime = audit.next.expirationDateTime;
            }
        }
    }

    public RecyclableReference<T> recyclableReference() {
        return compareAndSwapIfExpired().retrieve();
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
    private static final class RecyclableReferenceEnvelope<T> implements Serializable {
        @Serial
        private static final long serialVersionUID = 731224291034674365L;
        private final RecyclableReference.Factory<T> referenceFactory;
        private final long expirationDateTime;
        private final AtomicBoolean initializedCas = new AtomicBoolean();
        private volatile RecyclableReference<T> recyclableReference = null;

        private RecyclableReference<T> retrieve() {
            RecyclableReference<T> recyclableReferenceFixed;

            if (!initializedCas.compareAndSet(false, true)) {
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
    private static final class RecyclableReferenceEnvelopeAudit<T> {
        private RecyclableReferenceEnvelope<T> previous;
        private RecyclableReferenceEnvelope<T> next;
    }
}