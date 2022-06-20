package com.dipasquale.synchronization.event.loop;

@FunctionalInterface
interface EventLoopFactory {
    EventLoop create(EventLoopParams params, EventLoop entryPoint);

    @FunctionalInterface
    interface Proxy {
        EventLoop create(EventLoop entryPoint);
    }
}
