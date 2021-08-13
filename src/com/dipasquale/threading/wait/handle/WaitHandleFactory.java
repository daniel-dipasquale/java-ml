/*
 * java-ml
 * (c) 2021 daniel-dipasquale
 * released under the MIT license
 */

package com.dipasquale.threading.wait.handle;

@FunctionalInterface
public interface WaitHandleFactory<TItem, TWaitHandle extends WaitHandle> {
    TWaitHandle create(TItem item);
}
