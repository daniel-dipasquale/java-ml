/*
 * java-ml
 * (c) 2021 daniel-dipasquale
 * released under the MIT license
 */

package com.dipasquale.common.concurrent;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Generated;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Generated
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@Getter
@EqualsAndHashCode
@ToString
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
