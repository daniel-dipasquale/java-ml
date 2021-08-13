/*
 * java-ml
 * (c) 2021 daniel-dipasquale
 * released under the MIT license
 */

package com.dipasquale.threading.event.loop;

@FunctionalInterface
interface EventLoopFactory {
    EventLoop create(String name, EventLoopParams params, EventLoop nextEntryPoint);

    @FunctionalInterface
    interface Proxy {
        EventLoop create(EventLoop nextEntryPoint);
    }
}
