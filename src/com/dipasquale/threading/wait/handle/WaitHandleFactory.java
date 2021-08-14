package com.dipasquale.threading.wait.handle;

@FunctionalInterface
public interface WaitHandleFactory<TItem, TWaitHandle extends WaitHandle> {
    TWaitHandle create(TItem item);
}
