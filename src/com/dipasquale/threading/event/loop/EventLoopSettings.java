package com.dipasquale.threading.event.loop;

import com.dipasquale.common.DateTimeSupport;
import com.dipasquale.common.ExceptionLogger;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.locks.ReentrantLock;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
public final class EventLoopSettings {
    private static final Map<EventLoopType, EventLoopFactoryProxy> EVENT_LOOP_FACTORY_PROXIES = createEventLoopFactoryProxies();
    private static final Map<Boolean, EventLoopRecordQueueFactory> EVENT_RECORDS_FACTORIES = createEventRecordsFactories();
    @Builder.Default
    private final DateTimeSupport dateTimeSupport = DateTimeSupport.createMilliseconds();
    @Builder.Default
    private final EventLoopType type = EventLoopType.EXPLICIT_DELAY;
    private final String name;
    @Builder.Default
    private final int count = 1;
    private final EventLoopSelector selector;
    private final ExceptionLogger exceptionLogger;
    @Builder.Default
    private final boolean contended = true;
    private final ExecutorService executorService;

    private static Map<EventLoopType, EventLoopFactoryProxy> createEventLoopFactoryProxies() {
        Map<EventLoopType, EventLoopFactoryProxy> eventLoopFactories = new EnumMap<>(EventLoopType.class);

        eventLoopFactories.put(EventLoopType.EXPLICIT_DELAY, (name, eventRecordsFactory, params, nextEventLoop) -> {
            Queue<EventLoopRecord> queue = new PriorityQueue<>(Comparator.comparing(EventLoopRecord::getExecutionDateTime));
            ExclusiveQueue<EventLoopRecord> eventRecords = eventRecordsFactory.create(queue);

            return new EventLoopDefault(name, eventRecords, params, nextEventLoop);
        });

        eventLoopFactories.put(EventLoopType.NO_DELAY, EventLoopNoDelay::new);

        return eventLoopFactories;
    }

    private static Map<Boolean, EventLoopRecordQueueFactory> createEventRecordsFactories() {
        Map<Boolean, EventLoopRecordQueueFactory> eventLoopFactories = new HashMap<>();

        eventLoopFactories.put(true, q -> new ExclusiveQueueLocked<>(new ReentrantLock(), q));
        eventLoopFactories.put(false, ExclusiveQueueUnlocked::new);

        return eventLoopFactories;
    }

    EventLoopFactoryProxy getFactoryProxy() {
        return EVENT_LOOP_FACTORY_PROXIES.get(type);
    }

    EventLoopRecordQueueFactory getEventRecordsFactory() {
        return EVENT_RECORDS_FACTORIES.get(contended);
    }
}
