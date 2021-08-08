package com.dipasquale.threading.event.loop;

import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Queue;

final class ExplicitDelayEventLoopFactoryProxy implements EventLoopFactoryProxy {
    @Override
    public EventLoop create(final String name, final ExclusiveRecordQueueFactory recordQueueFactory, final DefaultEventLoopParams params, final EventLoop nextEventLoop) {
        Queue<Record> queue = new PriorityQueue<>(Comparator.comparing(Record::getExecutionDateTime));
        ExclusiveQueue<Record> recordQueue = recordQueueFactory.create(queue);

        return new DefaultEventLoop(name, recordQueue, params, nextEventLoop);
    }
}
