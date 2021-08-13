/*
 * java-ml
 * (c) 2021 daniel-dipasquale
 * released under the MIT license
 */

package com.dipasquale.common.error;

@FunctionalInterface
public interface FieldAccessor<T extends Throwable> {
    Object get(T error);
}
