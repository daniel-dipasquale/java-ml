/*
 * java-ml
 * (c) 2021 daniel-dipasquale
 * released under the MIT license
 */

package com.dipasquale.common.error;

@FunctionalInterface
public interface ErrorLogger {
    void log(Throwable throwable);
}
