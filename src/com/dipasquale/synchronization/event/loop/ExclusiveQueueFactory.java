package com.dipasquale.synchronization.event.loop;

import java.util.Queue;

@FunctionalInterface
interface ExclusiveQueueFactory<T> {
    ExclusiveQueue<T> create(Queue<T> queue);
}
