package com.dipasquale.ai.rl.neat;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter(AccessLevel.PACKAGE)
public final class Sequence<T> {
    private final List<T> values;

    public static <T> Sequence.Builder<T> builder() {
        return new Sequence.Builder<>();
    }

    public static final class Builder<T> {
        private final List<T> values = new ArrayList<>();

        public Sequence.Builder<T> add(final int quantity, final T value) {
            for (int i = 0; i < quantity; i++) {
                values.add(value);
            }

            return this;
        }

        public Sequence<T> build() {
            return new Sequence<>(List.copyOf(values));
        }
    }
}
