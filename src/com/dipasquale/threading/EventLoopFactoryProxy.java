package com.dipasquale.threading;

@FunctionalInterface
interface EventLoopFactoryProxy {
    EventLoop create(EventLoopDefaultParams params, EventLoopRecordQueueFactory eventRecordsFactory, String name, EventLoop nextEventLoop);
}
