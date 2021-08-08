package com.dipasquale.threading.event.loop;

@FunctionalInterface
interface EventLoopFactoryProxy {
    EventLoop create(String name, ExclusiveRecordQueueFactory recordQueueFactory, DefaultEventLoopParams params, EventLoop nextEventLoop);
}
