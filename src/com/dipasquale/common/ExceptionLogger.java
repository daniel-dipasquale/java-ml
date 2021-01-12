package com.dipasquale.common;

@FunctionalInterface
public interface ExceptionLogger {
    void log(Throwable throwable);
}
