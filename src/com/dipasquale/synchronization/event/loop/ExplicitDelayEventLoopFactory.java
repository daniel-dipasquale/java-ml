package com.dipasquale.synchronization.event.loop;

import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Queue;

final class ExplicitDelayEventLoopFactory implements EventLoopFactory {
    @Override
    public EventLoop create(final String name, final EventLoopParams params, final EventLoop nextEntryPoint) {
        Queue<EventRecord> underlyingQueue = new PriorityQueue<>(Comparator.comparing(EventRecord::getExecutionDateTime));
        ExclusiveQueue<EventRecord> eventRecordQueue = params.getEventRecordQueueFactory().create(underlyingQueue);

        ExplicitDelayEventLoopParams paramsFixed = ExplicitDelayEventLoopParams.builder()
                .eventRecordQueue(eventRecordQueue)
                .executorService(params.getExecutorService())
                .dateTimeSupport(params.getDateTimeSupport())
                .errorHandler(params.getErrorHandler())
                .build();

        return new ExplicitDelayEventLoop(name, paramsFixed, nextEntryPoint);
    }
}
