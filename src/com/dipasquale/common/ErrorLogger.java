package com.dipasquale.common;

@FunctionalInterface
public interface ErrorLogger {
    void log(Throwable throwable);
}
