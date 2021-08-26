package com.dipasquale.synchronization.wait.handle;

@FunctionalInterface
public interface WaitHandleFactory<TItem, TWaitHandle extends WaitHandle> {
    TWaitHandle create(TItem item);
}
