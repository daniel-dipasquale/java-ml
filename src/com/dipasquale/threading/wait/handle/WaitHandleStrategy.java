/*
 * java-ml
 * (c) 2021 daniel-dipasquale
 * released under the MIT license
 */

package com.dipasquale.threading.wait.handle;

@FunctionalInterface
public interface WaitHandleStrategy {
    boolean shouldAwait(int attempt);
}
