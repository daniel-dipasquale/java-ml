package com.dipasquale.threading.event.loop;

@FunctionalInterface
interface EventLoopFactoryProxy {
    EventLoop create(String name, ExclusiveQueueFactory<EventRecord> eventRecordQueueFactory, DefaultEventLoopParams params, EventLoop nextEntryPoint);
}
