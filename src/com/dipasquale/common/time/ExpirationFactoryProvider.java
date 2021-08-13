/*
 * java-ml
 * (c) 2021 daniel-dipasquale
 * released under the MIT license
 */

package com.dipasquale.common.time;

public interface ExpirationFactoryProvider {
    ExpirationFactory get(int index);

    int size();
}
