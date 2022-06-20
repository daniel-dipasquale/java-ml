package com.dipasquale.synchronization.event.loop;

import com.dipasquale.common.error.ErrorHandler;
import com.dipasquale.common.time.DateTimeSupport;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.concurrent.ExecutorService;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
public final class ParallelEventLoopSettings {
    private final ExecutorService executorService;
    private final int numberOfThreads;
    private final ErrorHandler errorHandler;
    private final DateTimeSupport dateTimeSupport;
}
