package com.dipasquale.common.concurrent;

import com.dipasquale.common.factory.ObjectFactory;

import java.io.Serial;
import java.io.Serializable;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public final class AtomicCoalescingReference<T> implements Serializable {
    @Serial
    private static final long serialVersionUID = -3970323341229979829L;
    private static final boolean FAIR = false;
    private final ReadWriteLock readWriteLock;
    private final AtomicBoolean invokingReferenceFactory;
    private final ObjectFactory<T> referenceFactory;
    private final AtomicReference<ReferenceContainer<T>> referenceContainer;

    public AtomicCoalescingReference(final boolean fairLock, final T reference, final ObjectFactory<T> referenceFactory) {
        this.readWriteLock = new ReentrantReadWriteLock(fairLock);
        this.invokingReferenceFactory = new AtomicBoolean();
        this.referenceFactory = referenceFactory;
        this.referenceContainer = new AtomicReference<>(new ReferenceContainer<>(reference, null));
    }

    public AtomicCoalescingReference(final T reference, final ObjectFactory<T> referenceFactory) {
        this(FAIR, reference, referenceFactory);
    }

    public T get() {
        readWriteLock.readLock().lock();

        try {
            return ReferenceContainer.coalesce(invokingReferenceFactory, referenceFactory, referenceContainer).resolve();
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    public void set(final T reference) {
        readWriteLock.writeLock().lock();

        try {
            referenceContainer.set(new ReferenceContainer<>(reference, null));
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }
}
