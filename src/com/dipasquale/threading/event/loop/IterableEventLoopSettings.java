/*
 * java-ml
 * (c) 2021 daniel-dipasquale
 * released under the MIT license
 */

package com.dipasquale.threading.event.loop;

import com.dipasquale.common.error.ErrorLogger;
import com.dipasquale.common.time.DateTimeSupport;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.concurrent.ExecutorService;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
public final class IterableEventLoopSettings {
    private final ExecutorService executorService;
    private final int numberOfThreads;
    private final ErrorLogger errorLogger;
    private final DateTimeSupport dateTimeSupport;
}
