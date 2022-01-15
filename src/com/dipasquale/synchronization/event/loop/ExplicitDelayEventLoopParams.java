package com.dipasquale.synchronization.event.loop;

import com.dipasquale.common.error.ErrorHandler;
import com.dipasquale.common.time.DateTimeSupport;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Queue;
import java.util.concurrent.ExecutorService;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PACKAGE)
@Getter
final class ExplicitDelayEventLoopParams {
    private final Queue<EventLoopRecord> eventLoopRecords;
    private final ExecutorService executorService;
    private final DateTimeSupport dateTimeSupport;
    private final ErrorHandler errorHandler;
}
