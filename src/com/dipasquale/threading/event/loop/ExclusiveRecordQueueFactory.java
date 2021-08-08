package com.dipasquale.threading.event.loop;

import java.util.Queue;

@FunctionalInterface
interface ExclusiveRecordQueueFactory {
    ExclusiveQueue<Record> create(Queue<Record> queue);
}
