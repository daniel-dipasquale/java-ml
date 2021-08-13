/*
 * java-ml
 * (c) 2021 daniel-dipasquale
 * released under the MIT license
 */

package com.dipasquale.common.error;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Generated;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Generated
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public final class FieldComparer<T extends Throwable> {
    private final FieldAccessor<T> accessor;
    @EqualsAndHashCode.Include
    @ToString.Include
    private final Object value;

    Object getValue(final T error) {
        return accessor.get(error);
    }
}
