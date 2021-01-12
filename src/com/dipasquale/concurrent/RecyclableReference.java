package com.dipasquale.concurrent;

import lombok.Generated;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Generated
@RequiredArgsConstructor
@Getter
public final class RecyclableReference<T> {
    private final T reference;
    private final long recycledDateTime;

    @FunctionalInterface
    public interface Factory<T> {
        T create(long dateTime);
    }

    @FunctionalInterface
    public interface Collector<T> {
        void collect(RecyclableReference<T> reference);
    }
}
