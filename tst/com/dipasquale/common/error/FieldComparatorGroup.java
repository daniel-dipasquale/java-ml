package com.dipasquale.common.error;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Generated;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Objects;

@Generated
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class FieldComparatorGroup<T extends Throwable> {
    private final T error;
    private final List<FieldComparator<T>> fields;

    @Builder(access = AccessLevel.PACKAGE)
    private static <T extends Throwable> FieldComparatorGroup<T> create(final List<FieldComparator<T>> fields) {
        return new FieldComparatorGroup<>(null, fields);
    }

    private void initialize(final FieldComparatorGroup<T> target, final FieldComparatorGroup<T> source) {
        target.fields.clear();

        source.fields.stream()
                .map(fieldComparator -> new FieldComparator<T>(null, fieldComparator.getValue(target.error)))
                .forEach(target.fields::add);
    }

    private boolean equals(final FieldComparatorGroup<T> other) {
        if (error != null) {
            initialize(this, other);
        } else {
            initialize(other, this);
        }

        return Objects.deepEquals(fields, other.fields);
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }

        if (other instanceof FieldComparatorGroup<?>) {
            return equals((FieldComparatorGroup<T>) other);
        }

        return false;
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
