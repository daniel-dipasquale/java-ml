package com.dipasquale.synchronization.event.loop;

import java.util.function.Consumer;

@FunctionalInterface
public interface ElementHandler<T> {
    boolean handle(T element);

    static <T> ElementHandler<T> adapt(final Consumer<T> elementHandler) {
        return element -> {
            elementHandler.accept(element);

            return true;
        };
    }
}
