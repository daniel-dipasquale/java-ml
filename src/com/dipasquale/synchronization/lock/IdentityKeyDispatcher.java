package com.dipasquale.synchronization.lock;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class IdentityKeyDispatcher<T extends Comparable<T>> implements KeyDispatcher<T, T> {
    @Override
    public T dispatch(final T value) {
        return value;
    }

    @Override
    public T recall(final T key) {
        return key;
    }
}
