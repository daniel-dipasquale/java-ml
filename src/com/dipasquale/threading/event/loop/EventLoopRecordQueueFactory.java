package com.dipasquale.threading.event.loop;

import java.util.Queue;

@FunctionalInterface
interface EventLoopRecordQueueFactory {
    ExclusiveQueue<EventLoopRecord> create(Queue<EventLoopRecord> queue);
}
