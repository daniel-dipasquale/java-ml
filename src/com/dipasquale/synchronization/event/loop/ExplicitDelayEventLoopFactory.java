package com.dipasquale.synchronization.event.loop;

import java.util.Comparator;
import java.util.PriorityQueue;

final class ExplicitDelayEventLoopFactory implements EventLoopFactory {
    @Override
    public EventLoop create(final String name, final EventLoopParams params, final EventLoop nextEntryPoint) {
        ExplicitDelayEventLoopParams paramsFixed = ExplicitDelayEventLoopParams.builder()
                .eventRecords(new PriorityQueue<>(Comparator.comparing(EventRecord::getExecutionDateTime)))
                .executorService(params.getExecutorService())
                .dateTimeSupport(params.getDateTimeSupport())
                .errorHandler(params.getErrorHandler())
                .build();

        return new ExplicitDelayEventLoop(name, paramsFixed, nextEntryPoint);
    }
}
