package com.dipasquale.threading.event.loop;

import com.dipasquale.common.error.ErrorLogger;
import com.dipasquale.common.time.DateTimeSupport;
import com.dipasquale.common.time.MillisecondsDateTimeSupport;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.locks.ReentrantLock;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
public final class EventLoopSettings {
    private static final Map<EventLoopType, EventLoopFactoryProxy> EVENT_LOOP_FACTORY_PROXIES = createEventLoopFactoryProxies();
    private static final Map<Boolean, ExclusiveQueueFactory<EventRecord>> EVENT_RECORD_QUEUE_FACTORIES = createEventRecordQueueFactories();
    private final ExecutorService executorService;
    @Builder.Default
    private final DateTimeSupport dateTimeSupport = new MillisecondsDateTimeSupport();
    @Builder.Default
    private final EventLoopType type = EventLoopType.EXPLICIT_DELAY;
    private final String name;
    @Builder.Default
    private final int concurrencyLevel = 1;
    private final EventLoopSelector eventLoopSelector;
    private final ErrorLogger errorLogger;
    @Builder.Default
    private final boolean contended = true;

    private static Map<EventLoopType, EventLoopFactoryProxy> createEventLoopFactoryProxies() {
        Map<EventLoopType, EventLoopFactoryProxy> eventLoopFactories = new EnumMap<>(EventLoopType.class);

        eventLoopFactories.put(EventLoopType.EXPLICIT_DELAY, new ExplicitDelayEventLoopFactoryProxy());
        eventLoopFactories.put(EventLoopType.NO_DELAY, NoDelayEventLoop::new);

        return eventLoopFactories;
    }

    private static Map<Boolean, ExclusiveQueueFactory<EventRecord>> createEventRecordQueueFactories() {
        Map<Boolean, ExclusiveQueueFactory<EventRecord>> eventRecordQueueFactories = new HashMap<>();

        eventRecordQueueFactories.put(true, erq -> new LockedExclusiveQueue<>(new ReentrantLock(), erq));
        eventRecordQueueFactories.put(false, UnlockedExclusiveQueue::new);

        return eventRecordQueueFactories;
    }

    EventLoopFactoryProxy getFactoryProxy() {
        return EVENT_LOOP_FACTORY_PROXIES.get(type);
    }

    ExclusiveQueueFactory<EventRecord> getEventRecordQueueFactory() {
        return EVENT_RECORD_QUEUE_FACTORIES.get(contended);
    }
}
