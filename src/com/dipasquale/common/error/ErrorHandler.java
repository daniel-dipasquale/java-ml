package com.dipasquale.common.error;

@FunctionalInterface
public interface ErrorHandler {
    boolean handle(Throwable throwable);
}
