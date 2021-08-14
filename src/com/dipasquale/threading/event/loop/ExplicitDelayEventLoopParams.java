package com.dipasquale.threading.event.loop;

import com.dipasquale.common.error.ErrorHandler;
import com.dipasquale.common.time.DateTimeSupport;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.concurrent.ExecutorService;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PACKAGE)
@Getter
final class ExplicitDelayEventLoopParams {
    private final ExclusiveQueue<EventRecord> eventRecordQueue;
    private final ExecutorService executorService;
    private final DateTimeSupport dateTimeSupport;
    private final ErrorHandler errorHandler;
}
