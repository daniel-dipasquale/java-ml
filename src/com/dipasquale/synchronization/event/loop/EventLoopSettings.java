package com.dipasquale.synchronization.event.loop;

import com.dipasquale.common.error.ErrorHandler;
import com.dipasquale.common.time.DateTimeSupport;
import com.dipasquale.common.time.MillisecondsDateTimeSupport;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.EnumMap;
import java.util.Map;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
public final class EventLoopSettings {
    private static final Map<EventLoopType, EventLoopFactory> EVENT_LOOP_FACTORIES = createEventLoopFactories();
    @Builder.Default
    private final DateTimeSupport dateTimeSupport = new MillisecondsDateTimeSupport();
    @Builder.Default
    private final EventLoopType type = EventLoopType.EXPLICIT_DELAY;
    @Builder.Default
    private final int numberOfThreads = 1;
    private final EventLoopSelector selector;
    private final ErrorHandler errorHandler;

    private static Map<EventLoopType, EventLoopFactory> createEventLoopFactories() {
        Map<EventLoopType, EventLoopFactory> eventLoopFactories = new EnumMap<>(EventLoopType.class);

        eventLoopFactories.put(EventLoopType.EXPLICIT_DELAY, new ExplicitDelayEventLoopFactory());
        eventLoopFactories.put(EventLoopType.ZERO_DELAY, ZeroDelayEventLoop::new);

        return eventLoopFactories;
    }

    EventLoopFactory getFactory() {
        return EVENT_LOOP_FACTORIES.get(type);
    }
}
