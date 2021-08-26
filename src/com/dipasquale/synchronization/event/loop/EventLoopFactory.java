package com.dipasquale.synchronization.event.loop;

@FunctionalInterface
interface EventLoopFactory {
    EventLoop create(String name, EventLoopParams params, EventLoop nextEntryPoint);

    @FunctionalInterface
    interface Proxy {
        EventLoop create(EventLoop nextEntryPoint);
    }
}
