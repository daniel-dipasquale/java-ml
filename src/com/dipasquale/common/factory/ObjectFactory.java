/*
 * java-ml
 * (c) 2021 daniel-dipasquale
 * released under the MIT license
 */

package com.dipasquale.common.factory;

@FunctionalInterface
public interface ObjectFactory<T> {
    T create();
}
