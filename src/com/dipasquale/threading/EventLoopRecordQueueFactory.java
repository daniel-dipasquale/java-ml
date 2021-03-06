package com.dipasquale.threading;

import java.util.Queue;

@FunctionalInterface
interface EventLoopRecordQueueFactory {
    ExclusiveQueue<EventLoopRecord> create(Queue<EventLoopRecord> queue);
}
