/*
 * java-ml
 * (c) 2021 daniel-dipasquale
 * released under the MIT license
 */

package com.dipasquale.threading.event.loop;

import com.dipasquale.common.error.ErrorLogger;
import com.dipasquale.common.time.DateTimeSupport;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.concurrent.ExecutorService;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PACKAGE)
@Getter
final class EventLoopParams {
    private final ExclusiveQueueFactory<EventRecord> eventRecordQueueFactory;
    private final ExecutorService executorService;
    private final DateTimeSupport dateTimeSupport;
    private final ErrorLogger errorLogger;
}
