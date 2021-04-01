package com.dipasquale.ai.rl.neat;

import com.google.common.collect.Lists;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class SettingsEnum<T extends Enum<T>> {
    private final Supplier<T> supplier;

    public static <T extends Enum<T>> SettingsEnum<T> literal(final T value) {
        return new SettingsEnum<>(() -> value);
    }

    private static <T extends Enum<T>> SettingsEnum<T> createRandom(final T[] constants) {
        List<T> values = Lists.newArrayList(constants);
        int size = values.size();

        Supplier<T> supplier = () -> {
            int index = SettingsConstants.RANDOM_SUPPORT_UNIFORM.next(0, size);

            return values.get(index);
        };

        return new SettingsEnum<>(supplier);
    }

    public static <T extends Enum<T>> SettingsEnum<T> random(final Class<T> type) {
        return createRandom(type.getEnumConstants());
    }

    public static <T extends Enum<T>> SettingsEnum<T> random(final T... constants) {
        return createRandom(constants);
    }

    T get() {
        return supplier.get();
    }

    @FunctionalInterface
    public interface Supplier<T extends Enum<T>> {
        T get();
    }
}
