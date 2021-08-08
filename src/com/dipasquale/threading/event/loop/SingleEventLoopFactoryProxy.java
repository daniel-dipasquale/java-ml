package com.dipasquale.threading.event.loop;

@FunctionalInterface
interface SingleEventLoopFactoryProxy {
    EventLoop create(EventLoop nextEventLoop);
}
