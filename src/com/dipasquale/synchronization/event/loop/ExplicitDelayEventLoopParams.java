package com.dipasquale.synchronization.event.loop;

import com.dipasquale.common.error.ErrorHandler;
import com.dipasquale.common.time.DateTimeSupport;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Queue;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PACKAGE)
@Getter
final class ExplicitDelayEventLoopParams {
    private final Queue<EventRecord> eventRecords;
    private final DateTimeSupport dateTimeSupport;
    private final ErrorHandler errorHandler;
}
