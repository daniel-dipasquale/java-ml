/*
 * java-ml
 * (c) 2021 daniel-dipasquale
 * released under the MIT license
 */

package com.dipasquale.threading.event.loop;

import java.util.Queue;

@FunctionalInterface
interface ExclusiveQueueFactory<T> {
    ExclusiveQueue<T> create(Queue<T> queue);
}
