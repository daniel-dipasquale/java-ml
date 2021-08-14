package com.dipasquale.common.error;

@FunctionalInterface
public interface ErrorLogger {
    void log(Throwable throwable);
}
