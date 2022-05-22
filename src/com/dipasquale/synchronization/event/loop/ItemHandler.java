package com.dipasquale.synchronization.event.loop;

import java.util.function.Consumer;

@FunctionalInterface
public interface ItemHandler<T> {
    boolean handle(EventLoopId id, T item);

    static <T> ItemHandler<T> createProxy(final Consumer<T> itemHandler) {
        return (id, item) -> {
            itemHandler.accept(item);

            return true;
        };
    }
}
