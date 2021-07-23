package com.dipasquale.threading.event.loop;

import com.dipasquale.common.DateTimeSupport;
import com.dipasquale.common.ErrorLogger;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.concurrent.ExecutorService;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
public final class EventLoopIterableSettings {
    private final ExecutorService executorService;
    private final int numberOfThreads;
    private final ErrorLogger errorLogger;
    private final DateTimeSupport dateTimeSupport;
}
