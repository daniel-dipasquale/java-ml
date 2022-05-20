package com.dipasquale.synchronization.lock;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class InternalLockRing<TInternalKey extends Comparable<TInternalKey>, TPublicKey> extends AbstractLockRing<TInternalKey, Lock> implements LockRing<TPublicKey, Lock> {
    @Serial
    private static final long serialVersionUID = 972124250995825688L;
    private final KeyDispatcher<TInternalKey, TPublicKey> keyDispatcher;

    @Override
    public Lock createLock(final TPublicKey key) {
        TInternalKey internalKey = keyDispatcher.dispatch(key);
        Lock underlyingLock = new ReentrantLock();

        return createLock(internalKey, underlyingLock, underlyingLock);
    }

    @Override
    public TPublicKey identify(final Lock lock) {
        RegisteredLock<TInternalKey, Lock> registeredLock = (RegisteredLock<TInternalKey, Lock>) lock;
        TInternalKey internalKey = registeredLock.getKey();

        return keyDispatcher.recall(internalKey);
    }
}
