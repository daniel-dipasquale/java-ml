package com.dipasquale.synchronization.event.loop;

import java.util.Comparator;
import java.util.PriorityQueue;

final class ExplicitDelayEventLoopFactory implements EventLoopFactory {
    private static final Comparator<EventRecord> COMPARATOR = Comparator.comparing(EventRecord::getExecutionDateTime);

    @Override
    public EventLoop create(final EventLoopParams params, final EventLoop entryPoint) {
        ExplicitDelayEventLoopParams fixedParams = ExplicitDelayEventLoopParams.builder()
                .eventRecords(new PriorityQueue<>(COMPARATOR))
                .dateTimeSupport(params.getDateTimeSupport())
                .errorHandler(params.getErrorHandler())
                .build();

        return new ExplicitDelayEventLoop(fixedParams, entryPoint);
    }
}
