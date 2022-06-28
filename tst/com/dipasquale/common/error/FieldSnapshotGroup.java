package com.dipasquale.common.error;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Generated;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Objects;

@Generated
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class FieldSnapshotGroup<T extends Throwable> {
    private final T error;
    private final List<FieldSnapshot<T>> fields;

    @Builder(access = AccessLevel.PACKAGE)
    private static <T extends Throwable> FieldSnapshotGroup<T> create(final List<FieldSnapshot<T>> fields) {
        return new FieldSnapshotGroup<>(null, fields);
    }

    private void initialize(final FieldSnapshotGroup<T> target, final FieldSnapshotGroup<T> source) {
        target.fields.clear();

        source.fields.stream()
                .map(fieldSnapshot -> new FieldSnapshot<T>(null, fieldSnapshot.getValue(target.error)))
                .forEach(target.fields::add);
    }

    private boolean equals(final FieldSnapshotGroup<T> other) {
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

        if (other instanceof FieldSnapshotGroup<?>) {
            return equals((FieldSnapshotGroup<T>) other);
        }

        return false;
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
