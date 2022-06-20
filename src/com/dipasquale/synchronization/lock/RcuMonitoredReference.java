package com.dipasquale.synchronization.lock;

@FunctionalInterface
public interface RcuMonitoredReference<T> {
    T get();
}
