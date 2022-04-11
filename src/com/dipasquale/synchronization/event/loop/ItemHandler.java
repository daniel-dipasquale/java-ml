package com.dipasquale.synchronization.event.loop;

import java.util.function.Consumer;

@FunctionalInterface
public interface ItemHandler<T> {
    boolean handle(String name, T item);

    static <T> ItemHandler<T> proxy(final Consumer<T> itemHandler) {
        return (name, item) -> {
            itemHandler.accept(item);

            return true;
        };
    }
}
