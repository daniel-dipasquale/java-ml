package com.dipasquale.threading.event.loop;

import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Queue;

final class ExplicitDelayEventLoopFactoryProxy implements EventLoopFactoryProxy {
    @Override
    public EventLoop create(final String name, final ExclusiveQueueFactory<EventRecord> eventRecordQueueFactory, final DefaultEventLoopParams params, final EventLoop nextEntryPoint) {
        Queue<EventRecord> underlyingQueue = new PriorityQueue<>(Comparator.comparing(EventRecord::getExecutionDateTime));
        ExclusiveQueue<EventRecord> eventRecordQueue = eventRecordQueueFactory.create(underlyingQueue);

        return new DefaultEventLoop(name, eventRecordQueue, params, nextEntryPoint);
    }
}
