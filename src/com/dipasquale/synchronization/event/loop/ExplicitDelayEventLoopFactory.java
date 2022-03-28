package com.dipasquale.synchronization.event.loop;

import java.util.Comparator;
import java.util.PriorityQueue;

final class ExplicitDelayEventLoopFactory implements EventLoopFactory {
    private static final Comparator<EventLoopRecord> COMPARATOR = Comparator.comparing(EventLoopRecord::getExecutionDateTime);

    @Override
    public EventLoop create(final String name, final EventLoopParams params, final EventLoop entryPoint) {
        ExplicitDelayEventLoopParams fixedParams = ExplicitDelayEventLoopParams.builder()
                .eventLoopRecords(new PriorityQueue<>(COMPARATOR))
                .executorService(params.getExecutorService())
                .dateTimeSupport(params.getDateTimeSupport())
                .errorHandler(params.getErrorHandler())
                .build();

        return new ExplicitDelayEventLoop(name, fixedParams, entryPoint);
    }
}
