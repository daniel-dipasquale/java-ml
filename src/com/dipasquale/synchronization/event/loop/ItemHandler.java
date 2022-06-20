package com.dipasquale.synchronization.event.loop;

import java.util.function.Consumer;

@FunctionalInterface
public interface ItemHandler<T> {
    boolean handle(T item);

    static <T> ItemHandler<T> adapt(final Consumer<T> itemHandler) {
        return (item) -> {
            itemHandler.accept(item);

            return true;
        };
    }
}
