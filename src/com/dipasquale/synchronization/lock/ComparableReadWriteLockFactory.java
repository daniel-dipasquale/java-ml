package com.dipasquale.synchronization.lock;

import com.dipasquale.common.concurrent.ConcurrentId;
import com.dipasquale.common.factory.ObjectFactory;
import com.dipasquale.common.factory.concurrent.ConcurrentIdFactory;
import lombok.RequiredArgsConstructor;

import java.util.concurrent.locks.ReadWriteLock;

@RequiredArgsConstructor
public final class ComparableReadWriteLockFactory implements ObjectFactory<ComparableReadWriteLock<Long>> {
    private final ObjectFactory<ReadWriteLock> lockFactory;
    private final ConcurrentIdFactory concurrentIdFactory;

    @Override
    public ComparableReadWriteLock<Long> create() {
        ReadWriteLock readWriteLock = lockFactory.create();
        ConcurrentId<Long> concurrentId = concurrentIdFactory.createId();

        return new ComparableReadWriteLock<>(readWriteLock, concurrentId);
    }
}
