package com.dipasquale.threading;

@FunctionalInterface
interface EventLoopFactory {
    EventLoop create(EventLoop nextLoop);
}
