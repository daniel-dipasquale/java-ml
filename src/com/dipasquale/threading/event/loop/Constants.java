/*
 * java-ml
 * (c) 2021 daniel-dipasquale
 * released under the MIT license
 */

package com.dipasquale.threading.event.loop;

import lombok.Generated;

import java.util.concurrent.locks.ReentrantLock;

final class Constants {
    @Generated
    private Constants() {
    }

    static final ExclusiveQueueFactory<EventRecord> EVENT_RECORD_QUEUE_FACTORY = erq -> new LockedExclusiveQueue<>(new ReentrantLock(), erq);
}
