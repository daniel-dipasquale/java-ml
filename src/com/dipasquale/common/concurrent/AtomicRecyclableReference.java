package com.dipasquale.common.concurrent;

import com.dipasquale.common.ArgumentValidatorSupport;
import com.dipasquale.common.ObjectFactory;
import com.dipasquale.common.time.ExpirationFactory;
import com.dipasquale.common.time.ExpirationRecord;
import lombok.AccessLevel;
import lombok.Generated;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

public final class AtomicRecyclableReference<T> {
    private final RecyclableReference.Factory<T> referenceFactory;
    private final ExpirationFactory expirationFactory;
    private final AtomicReference<Envelope> envelopeCas;
    private final AtomicLong lastRecycledDateTime;
    private final Queue<RecyclableReference<T>> recycledReferences;
    private final RecyclableReference.Collector<T> recycledReferenceCollector;
    private final AtomicLong lastRecycledConfirmationDateTime;

    private AtomicRecyclableReference(final RecyclableReference.Factory<T> referenceFactory, final ExpirationFactory expirationFactory, final boolean collectRecycledReferences, final RecyclableReference.Collector<T> recycledReferenceCollector) {
        Queue<RecyclableReference<T>> recycledReferences = new ConcurrentLinkedQueue<>();

        this.referenceFactory = referenceFactory;
        this.expirationFactory = expirationFactory;
        this.envelopeCas = new AtomicReference<>();
        this.lastRecycledDateTime = new AtomicLong(Long.MIN_VALUE);
        this.recycledReferences = recycledReferences;
        this.recycledReferenceCollector = collectRecycledReferences ? recycledReferences::add : recycledReferenceCollector;
        this.lastRecycledConfirmationDateTime = new AtomicLong(Long.MIN_VALUE);
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
        this(edt -> referenceFactory.create(), expirationFactory, false, recycledReferenceCollector);
    }

    public AtomicRecyclableReference(final ObjectFactory<T> referenceFactory, final ExpirationFactory expirationFactory, final boolean collectRecycledReferences) {
        this(edt -> referenceFactory.create(), expirationFactory, collectRecycledReferences);
    }

    public AtomicRecyclableReference(final ObjectFactory<T> referenceFactory, final ExpirationFactory expirationFactory) {
        this(edt -> referenceFactory.create(), expirationFactory);
    }

    private Envelope recycleIfExpired(final Envelope envelope, final ExpirationRecord expirationRecord) {
        if (envelope != null && expirationRecord.getCurrentDateTime() < envelope.expiryDateTime) {
            return envelope;
        }

        if (!lastRecycledDateTime.compareAndSet(lastRecycledConfirmationDateTime.get(), expirationRecord.getExpirationDateTime())) {
            Envelope envelopeFixed = envelope;

            while (envelopeFixed == envelope) {
                envelopeFixed = envelopeCas.get();
            }

            return envelopeFixed;
        }

        if (envelope != null && recycledReferenceCollector != null) {
            recycledReferenceCollector.collect(envelope.getRecyclableReference());
        }

        return new Envelope(expirationRecord.getExpirationDateTime());
    }

    private Envelope recycleIfExpired(final Envelope envelope) {
        return recycleIfExpired(envelope, expirationFactory.create());
    }

    private Envelope compareAndSwapIfExpired() {
        EnvelopeAudit envelopeAudit = new EnvelopeAudit();

        try {
            return envelopeCas.accumulateAndGet(null, (oee, nee) -> {
                envelopeAudit.previous = oee;

                return envelopeAudit.next = recycleIfExpired(oee);
            });
        } finally {
            if (envelopeAudit.previous != envelopeAudit.next) {
                lastRecycledConfirmationDateTime.set(envelopeAudit.next.expiryDateTime);
            }
        }
    }

    public RecyclableReference<T> recyclableReference() {
        return compareAndSwapIfExpired().getRecyclableReference();
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
    public boolean equals(final Object object) {
        if (this == object) {
            return true;
        }

        if (object instanceof AtomicRecyclableReference<?>) {
            return equals((AtomicRecyclableReference<T>) object);
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

    private T createReference(final long expiryDateTime) {
        T reference = referenceFactory.create(expiryDateTime);

        ArgumentValidatorSupport.ensureNotNull(reference, "referenceFactory");

        return reference;
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private final class Envelope {
        private final long expiryDateTime;
        private boolean initialized = false;
        private RecyclableReference<T> recyclableReference = null;

        private synchronized RecyclableReference<T> getRecyclableReference() {
            if (initialized) {
                return recyclableReference;
            }

            initialized = true;
            recyclableReference = new RecyclableReference<>(createReference(expiryDateTime), expiryDateTime);

            return recyclableReference;
        }
    }

    @Generated
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    private final class EnvelopeAudit {
        private Envelope previous;
        private Envelope next;
    }
}