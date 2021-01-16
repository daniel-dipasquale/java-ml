package com.dipasquale.concurrent;

import com.dipasquale.common.ExpiryRecord;
import com.dipasquale.common.ExpirySupport;
import com.dipasquale.common.ObjectFactory;
import lombok.RequiredArgsConstructor;

import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

public final class AtomicRecyclableReference<T> {
    private final RecyclableReference.Factory<T> factory;
    private final ExpirySupport expirySupport;
    private final AtomicReference<ExpirableEnvelope> expirableEnvelopeCas;
    private final AtomicLong lastReferenceRecycledDateTime;
    private final Queue<RecyclableReference<T>> recycledReferences;
    private final RecyclableReference.Collector<T> recyclableReferenceCollector;
    private final AtomicLong lastReferenceRecycledDateTimeConfirmed;

    private AtomicRecyclableReference(final RecyclableReference.Factory<T> factory, final ExpirySupport expirySupport, final boolean collectRecycledReferences, final RecyclableReference.Collector<T> recyclableReferenceCollector) {
        Queue<RecyclableReference<T>> recycledReferences = new ConcurrentLinkedQueue<>();

        this.factory = factory;
        this.expirySupport = expirySupport;
        this.expirableEnvelopeCas = new AtomicReference<>();
        this.lastReferenceRecycledDateTime = new AtomicLong(Long.MIN_VALUE);
        this.recycledReferences = recycledReferences;
        this.recyclableReferenceCollector = collectRecycledReferences ? recycledReferences::add : recyclableReferenceCollector;
        this.lastReferenceRecycledDateTimeConfirmed = new AtomicLong(Long.MIN_VALUE);
    }

    public AtomicRecyclableReference(final RecyclableReference.Factory<T> factory, final ExpirySupport expirySupport, final RecyclableReference.Collector<T> recyclableReferenceCollector) {
        this(factory, expirySupport, false, recyclableReferenceCollector);
    }

    public AtomicRecyclableReference(final RecyclableReference.Factory<T> factory, final ExpirySupport expirySupport, final boolean collectRecycledReferences) {
        this(factory, expirySupport, collectRecycledReferences, null);
    }

    public AtomicRecyclableReference(final RecyclableReference.Factory<T> factory, final ExpirySupport expirySupport) {
        this(factory, expirySupport, false);
    }

    public AtomicRecyclableReference(final ObjectFactory<T> factory, final ExpirySupport expirySupport, final RecyclableReference.Collector<T> recyclableReferenceCollector) {
        this(edt -> factory.create(), expirySupport, false, recyclableReferenceCollector);
    }

    public AtomicRecyclableReference(final ObjectFactory<T> factory, final ExpirySupport expirySupport, final boolean collectRecycledReferences) {
        this(edt -> factory.create(), expirySupport, collectRecycledReferences);
    }

    public AtomicRecyclableReference(final ObjectFactory<T> factory, final ExpirySupport expirySupport) {
        this(edt -> factory.create(), expirySupport);
    }

    private static <T> RecyclableReference<T> retrieveRecyclableReference(final AtomicRecyclableReference<T>.ExpirableEnvelope expirableEnvelope) {
        AtomicRecyclableReference<T>.Envelope envelope = expirableEnvelope.envelope;

        if (envelope != null) {
            return envelope.recyclableReference;
        }

        return expirableEnvelope.ensureInitialized().recyclableReference;
    }

    private ExpirableEnvelope recycleIfExpired(final ExpirableEnvelope expirableEnvelope, final ExpiryRecord expiryRecord) {
        if (expirableEnvelope != null && !expiryRecord.isExpired(expirableEnvelope.expiryDateTime)) {
            return expirableEnvelope;
        }

        if (!lastReferenceRecycledDateTime.compareAndSet(lastReferenceRecycledDateTimeConfirmed.get(), expiryRecord.getExpiryDateTime())) {
            ExpirableEnvelope expirableEnvelopeFixed = expirableEnvelope;

            while (expirableEnvelopeFixed == expirableEnvelope) {
                expirableEnvelopeFixed = expirableEnvelopeCas.get();
            }

            return expirableEnvelopeFixed;
        }

        if (expirableEnvelope != null && recyclableReferenceCollector != null) {
            recyclableReferenceCollector.collect(retrieveRecyclableReference(expirableEnvelope));
        }

        return new ExpirableEnvelope(expiryRecord.getExpiryDateTime());
    }

    private ExpirableEnvelope recycleIfExpired(final ExpirableEnvelope expirableEnvelope) {
        return recycleIfExpired(expirableEnvelope, expirySupport.next());
    }

    private ExpirableEnvelope compareAndSwapIfExpired() {
        ExpirableEnvelopeAudit audit = new ExpirableEnvelopeAudit();

        try {
            return expirableEnvelopeCas.accumulateAndGet(null, (oee, nee) -> {
                ExpirableEnvelope expirableEnvelopeNew = recycleIfExpired(oee);

                audit.previous = oee;
                audit.next = expirableEnvelopeNew;

                return expirableEnvelopeNew;
            });
        } finally {
            if (audit.previous != audit.next) {
                lastReferenceRecycledDateTimeConfirmed.set(audit.next.expiryDateTime);
            }
        }
    }

    public RecyclableReference<T> recyclableReference() {
        ExpirableEnvelope expirableEnvelope = compareAndSwapIfExpired();

        return retrieveRecyclableReference(expirableEnvelope);
    }

    public T reference() {
        return recyclableReference().getReference();
    }

    public RecyclableReference<T> pollRecycled() {
        return recycledReferences.poll();
    }

    @Override
    public int hashCode() {
        return reference().hashCode();
    }

    @Override
    public boolean equals(final Object object) {
        if (this == object) {
            return true;
        }

        if (object instanceof AtomicRecyclableReference<?>) {
            return Objects.equals(reference(), ((AtomicRecyclableReference<?>) object).reference());
        }

        return false;
    }

    @Override
    public String toString() {
        return reference().toString();
    }

    @RequiredArgsConstructor
    private final class Envelope { // NOTE: the envelope is only needed if the factory provided is designed to sometimes produce null pointers, that way, we can rely on the envelope to determine whether a synchronization block is needed for the initialization of the reference
        private final RecyclableReference<T> recyclableReference;
    }

    @RequiredArgsConstructor
    private final class ExpirableEnvelope {
        private final long expiryDateTime;
        private boolean initialized = false;
        private volatile Envelope envelope = null;

        private RecyclableReference<T> createRecyclableReference() {
            return new RecyclableReference<>(factory.create(expiryDateTime), expiryDateTime);
        }

        public synchronized Envelope ensureInitialized() {
            if (initialized) {
                return envelope;
            }

            initialized = true;

            return envelope = new Envelope(createRecyclableReference());
        }
    }

    private final class ExpirableEnvelopeAudit {
        private ExpirableEnvelope previous;
        private ExpirableEnvelope next;
    }
}

/*
package com.pasqud.concurrent;

import com.pasqud.common.ExpiryRecord;
import com.pasqud.common.ExpirySupport;
import com.pasqud.common.ObjectFactory;
import lombok.RequiredArgsConstructor;

import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BinaryOperator;

public final class AtomicRecyclableReference<T> {
    private final RecyclableReference.Factory<T> factory;
    private final ExpirySupport expirySupport;
    private final AtomicReference<ExpirableEnvelope> expirableEnvelopeCas;
    private final AtomicLong lastReferenceRecycledDateTime;
    private final Queue<RecyclableReference<T>> recycledReferences;
    private final RecyclableReference.Collector<T> recyclableReferenceCollector;
    private final AtomicLong lastReferenceRecycledDateTimeConfirmed;
    private final ThreadLocal<CompareAndSwapState> compareAndSwapStateThreadLocal;

    private AtomicRecyclableReference(final RecyclableReference.Factory<T> factory, final ExpirySupport expirySupport, final boolean collectRecycledReferences, final RecyclableReference.Collector<T> recyclableReferenceCollector) {
        Queue<RecyclableReference<T>> recycledReferences = new ConcurrentLinkedQueue<>();

        this.factory = factory;
        this.expirySupport = expirySupport;
        this.expirableEnvelopeCas = new AtomicReference<>();
        this.lastReferenceRecycledDateTime = new AtomicLong(Long.MIN_VALUE);
        this.recycledReferences = recycledReferences;
        this.recyclableReferenceCollector = collectRecycledReferences ? recycledReferences::add : recyclableReferenceCollector;
        this.lastReferenceRecycledDateTimeConfirmed = new AtomicLong(Long.MIN_VALUE);
        this.compareAndSwapStateThreadLocal = ThreadLocal.withInitial(CompareAndSwapState::new);
    }

    public AtomicRecyclableReference(final RecyclableReference.Factory<T> factory, final ExpirySupport expirySupport, final RecyclableReference.Collector<T> recyclableReferenceCollector) {
        this(factory, expirySupport, false, recyclableReferenceCollector);
    }

    public AtomicRecyclableReference(final RecyclableReference.Factory<T> factory, final ExpirySupport expirySupport, final boolean collectRecycledReferences) {
        this(factory, expirySupport, collectRecycledReferences, null);
    }

    public AtomicRecyclableReference(final RecyclableReference.Factory<T> factory, final ExpirySupport expirySupport) {
        this(factory, expirySupport, false);
    }

    public AtomicRecyclableReference(final ObjectFactory<T> factory, final ExpirySupport expirySupport, final RecyclableReference.Collector<T> recyclableReferenceCollector) {
        this(edt -> factory.create(), expirySupport, false, recyclableReferenceCollector);
    }

    public AtomicRecyclableReference(final ObjectFactory<T> factory, final ExpirySupport expirySupport, final boolean collectRecycledReferences) {
        this(edt -> factory.create(), expirySupport, collectRecycledReferences);
    }

    public AtomicRecyclableReference(final ObjectFactory<T> factory, final ExpirySupport expirySupport) {
        this(edt -> factory.create(), expirySupport);
    }

    private static <T> RecyclableReference<T> retrieveRecyclableReference(final AtomicRecyclableReference<T>.ExpirableEnvelope expirableEnvelope) {
        AtomicRecyclableReference<T>.Envelope envelope = expirableEnvelope.envelope;

        if (envelope != null) {
            return envelope.recyclableReference;
        }

        return expirableEnvelope.ensureInitialized().recyclableReference;
    }

    private ExpirableEnvelope recycleIfExpired(final ExpirableEnvelope expirableEnvelope, final ExpiryRecord expiryRecord) {
        if (expirableEnvelope != null && !expiryRecord.isExpired(expirableEnvelope.expiryDateTime)) {
            return expirableEnvelope;
        }

        if (!lastReferenceRecycledDateTime.compareAndSet(lastReferenceRecycledDateTimeConfirmed.get(), expiryRecord.getExpiryDateTime())) {
            ExpirableEnvelope expirableEnvelopeFixed = expirableEnvelope;

            while (expirableEnvelopeFixed == expirableEnvelope) {
                expirableEnvelopeFixed = expirableEnvelopeCas.get();
            }

            return expirableEnvelopeFixed;
        }

        if (expirableEnvelope != null && recyclableReferenceCollector != null) {
            recyclableReferenceCollector.collect(retrieveRecyclableReference(expirableEnvelope));
        }

        return new ExpirableEnvelope(expiryRecord.getExpiryDateTime());
    }

    private ExpirableEnvelope recycleIfExpired(final ExpirableEnvelope expirableEnvelope) {
        return recycleIfExpired(expirableEnvelope, expirySupport.next());
    }

    private ExpirableEnvelope compareAndSwapIfExpired() {
        CompareAndSwapState lol = compareAndSwapStateThreadLocal.get();

        try {
            return expirableEnvelopeCas.accumulateAndGet(null, lol);
        } finally {
            lol.confirmSwapIfSuccessful();
        }
    }

    public RecyclableReference<T> recyclableReference() {
        ExpirableEnvelope expirableEnvelope = compareAndSwapIfExpired();

        return retrieveRecyclableReference(expirableEnvelope);
    }

    public T reference() {
        return recyclableReference().getReference();
    }

    public RecyclableReference<T> pollRecycled() {
        return recycledReferences.poll();
    }

    @Override
    public int hashCode() {
        return reference().hashCode();
    }

    @Override
    public boolean equals(final Object object) {
        if (this == object) {
            return true;
        }

        if (object instanceof AtomicRecyclableReference<?>) {
            return Objects.equals(reference(), ((AtomicRecyclableReference<?>) object).reference());
        }

        return false;
    }

    @Override
    public String toString() {
        return reference().toString();
    }

    @RequiredArgsConstructor
    private final class Envelope { // NOTE: the envelope is only needed if the factory provided is designed to sometimes produce null pointers, that way, we can rely on the envelope to determine whether a synchronization block is needed for the initialization of the reference
        private final RecyclableReference<T> recyclableReference;
    }

    @RequiredArgsConstructor
    private final class ExpirableEnvelope {
        private final long expiryDateTime;
        private boolean initialized = false;
        private volatile Envelope envelope = null;

        private RecyclableReference<T> createRecyclableReference() {
            return new RecyclableReference<>(factory.create(expiryDateTime), expiryDateTime);
        }

        public synchronized Envelope ensureInitialized() {
            if (initialized) {
                return envelope;
            }

            initialized = true;

            return envelope = new Envelope(createRecyclableReference());
        }
    }

    private final class CompareAndSwapState implements BinaryOperator<ExpirableEnvelope> {
        private final Object[] expirableEnvelopes = new Object[2];

        @Override
        public ExpirableEnvelope apply(final ExpirableEnvelope oldExpirableEnvelope, final ExpirableEnvelope newExpirableEnvelope) {
            ExpirableEnvelope expirableEnvelopeOverride = recycleIfExpired(oldExpirableEnvelope);

            expirableEnvelopes[0] = oldExpirableEnvelope;
            expirableEnvelopes[1] = expirableEnvelopeOverride;

            return expirableEnvelopeOverride;
        }

        public void confirmSwapIfSuccessful() {
            try {
                if (expirableEnvelopes[0] != expirableEnvelopes[1]) {
                    lastReferenceRecycledDateTimeConfirmed.set(((ExpirableEnvelope) expirableEnvelopes[1]).expiryDateTime);
                }
            } finally {
                expirableEnvelopes[0] = null;
                expirableEnvelopes[1] = null;
            }
        }
    }
}
 */