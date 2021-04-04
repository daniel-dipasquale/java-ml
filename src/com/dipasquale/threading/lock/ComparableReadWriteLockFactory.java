package com.dipasquale.threading.lock;

import com.dipasquale.common.ObjectFactory;
import com.dipasquale.concurrent.ConcurrentId;
import com.dipasquale.concurrent.ConcurrentIdFactory;
import lombok.RequiredArgsConstructor;

import java.util.concurrent.locks.ReadWriteLock;

@RequiredArgsConstructor
public final class ComparableReadWriteLockFactory implements ObjectFactory<ComparableReadWriteLock> {
    private final ObjectFactory<ReadWriteLock> lockFactory;
    private final ConcurrentIdFactory concurrentIdFactory;

    @Override
    public ComparableReadWriteLock create() {
        ReadWriteLock readWriteLock = lockFactory.create();
        ConcurrentId<Long> concurrentId = concurrentIdFactory.createId();

        return ComparableReadWriteLock.create(readWriteLock, concurrentId);
    }
}
