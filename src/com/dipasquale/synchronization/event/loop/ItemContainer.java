package com.dipasquale.synchronization.event.loop;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@Getter
public final class ItemContainer<T> {
    private final T item;
}
