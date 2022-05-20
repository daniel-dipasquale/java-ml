package com.dipasquale.synchronization.lock;

interface KeyDispatcher<TKey, TValue> {
    TKey dispatch(TValue value);

    TValue recall(TKey key);
}
