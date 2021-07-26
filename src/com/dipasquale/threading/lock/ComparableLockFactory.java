package com.dipasquale.threading.lock;

import com.dipasquale.common.concurrent.ConcurrentId;
import com.dipasquale.common.factory.ObjectFactory;
import com.dipasquale.common.factory.concurrent.ConcurrentIdFactory;
import lombok.RequiredArgsConstructor;

import java.util.concurrent.locks.Lock;

@RequiredArgsConstructor
public final class ComparableLockFactory implements ObjectFactory<ComparableLock> {
    private final ObjectFactory<Lock> lockFactory;
    private final ConcurrentIdFactory concurrentIdFactory;

    @Override
    public ComparableLock create() {
        Lock lock = lockFactory.create();
        ConcurrentId<Long> concurrentId = concurrentIdFactory.createId();

        return ComparableLock.create(lock, concurrentId);
    }
}
