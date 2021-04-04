package com.dipasquale.threading.lock;

import com.dipasquale.common.ObjectFactory;
import com.dipasquale.concurrent.ConcurrentId;
import com.dipasquale.concurrent.ConcurrentIdFactory;
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
