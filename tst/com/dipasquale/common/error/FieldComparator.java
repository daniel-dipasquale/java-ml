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
public final class FieldComparator<T extends Throwable> {
    private final FieldSelector<T> selector;
    @EqualsAndHashCode.Include
    @ToString.Include
    private final Object value;

    Object getValue(final T error) {
        return selector.get(error);
    }
}
