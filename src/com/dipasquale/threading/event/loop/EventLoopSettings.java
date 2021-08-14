package com.dipasquale.threading.event.loop;

import com.dipasquale.common.error.ErrorLogger;
import com.dipasquale.common.time.DateTimeSupport;
import com.dipasquale.common.time.MillisecondsDateTimeSupport;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
public final class EventLoopSettings {
    private static final Map<EventLoopType, EventLoopFactory> EVENT_LOOP_FACTORIES = createEventLoopFactories();
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

    private static Map<EventLoopType, EventLoopFactory> createEventLoopFactories() {
        Map<EventLoopType, EventLoopFactory> eventLoopFactories = new EnumMap<>(EventLoopType.class);

        eventLoopFactories.put(EventLoopType.EXPLICIT_DELAY, new ExplicitDelayEventLoopFactory());
        eventLoopFactories.put(EventLoopType.NO_DELAY, NoDelayEventLoop::new);

        return eventLoopFactories;
    }

    EventLoopFactory getFactory() {
        return EVENT_LOOP_FACTORIES.get(type);
    }
}
