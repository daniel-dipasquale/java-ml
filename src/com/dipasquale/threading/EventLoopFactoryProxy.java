package com.dipasquale.threading;

@FunctionalInterface
interface EventLoopFactoryProxy {
    EventLoop create(String name, EventLoopRecordQueueFactory eventRecordsFactory, EventLoopDefaultParams params, EventLoop nextEventLoop);
}
