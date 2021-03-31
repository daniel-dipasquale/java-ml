package com.dipasquale.threading;

import com.dipasquale.common.DateTimeSupport;
import com.dipasquale.common.ExceptionLogger;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.concurrent.ExecutorService;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@Builder(access = AccessLevel.PACKAGE)
@Getter
final class EventLoopDefaultParams {
    private final DateTimeSupport dateTimeSupport;
    private final ExceptionLogger exceptionLogger;
    private final ExecutorService executorService;
}
