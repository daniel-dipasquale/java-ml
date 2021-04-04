package com.dipasquale.threading.event.loop;

@FunctionalInterface
interface EventLoopFactory {
    EventLoop create(EventLoop nextLoop);
}
