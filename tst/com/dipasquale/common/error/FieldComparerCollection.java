package com.dipasquale.common.error;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Generated;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Objects;

@Generated
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class FieldComparerCollection<T extends Throwable> {
    private final T error;
    private final List<FieldComparer<T>> fields;

    @Builder(access = AccessLevel.PACKAGE)
    private static <T extends Throwable> FieldComparerCollection<T> create(final List<FieldComparer<T>> fields) {
        return new FieldComparerCollection<>(null, fields);
    }

    private void initialize(final FieldComparerCollection<T> target, final FieldComparerCollection<T> source) {
        target.fields.clear();

        source.fields.stream()
                .map(f -> new FieldComparer<T>(null, f.getValue(target.error)))
                .forEach(target.fields::add);
    }

    private boolean equals(final FieldComparerCollection<T> other) {
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

        if (other instanceof FieldComparerCollection<?>) {
            return equals((FieldComparerCollection<T>) other);
        }

        return false;
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
