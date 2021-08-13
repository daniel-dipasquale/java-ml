/*
 * java-ml
 * (c) 2021 daniel-dipasquale
 * released under the MIT license
 */

package com.dipasquale.threading.event.loop;

@FunctionalInterface
public interface EventLoopHandler {
    void handle(String name);
}
