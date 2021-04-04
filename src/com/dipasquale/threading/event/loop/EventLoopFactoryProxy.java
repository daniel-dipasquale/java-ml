package com.dipasquale.threading.event.loop;

@FunctionalInterface
interface EventLoopFactoryProxy {
    EventLoop create(String name, EventLoopRecordQueueFactory eventRecordsFactory, EventLoopDefaultParams params, EventLoop nextEventLoop);
}
