package com.dipasquale.ai.rl.neat;

import com.dipasquale.ai.rl.neat.context.Context;
import com.dipasquale.ai.rl.neat.context.ContextDefaultComponentFactory;
import com.dipasquale.ai.rl.neat.context.ContextDefaultParallelism;
import com.dipasquale.threading.EventLoop;
import com.dipasquale.threading.EventLoopStream;
import com.dipasquale.threading.EventLoopStreamSettings;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public final class SettingsParallelism {
    private final ExecutorService executorService;
    private final int numberOfThreads;

    boolean isEnabled() {
        return executorService != null;
    }

    int getNumberOfThreads() {
        if (executorService == null) {
            return 1;
        }

        if (numberOfThreads > 0) {
            return numberOfThreads;
        }

        return Math.max(1, Runtime.getRuntime().availableProcessors() - 1);
    }

    ContextDefaultComponentFactory<ContextDefaultParallelism> createFactory() {
        return context -> {
            if (!isEnabled()) {
                Context.Parallelism parallelism = new ContextDefaultParallelism.SingleThread();

                return new ContextDefaultParallelism(parallelism);
            }

            List<Throwable> exceptions = Collections.unmodifiableList(new ArrayList<>());

            EventLoopStreamSettings settings = EventLoopStreamSettings.builder()
                    .executorService(executorService)
                    .numberOfThreads(getNumberOfThreads())
                    .exceptionLogger(exceptions::add)
                    .dateTimeSupport(SettingsConstants.DATE_TIME_SUPPORT_MILLISECONDS)
                    .build();

            EventLoopStream eventLoopStream = EventLoop.createStream(settings);
            Context.Parallelism parallelism = new ContextDefaultParallelism.MultiThread(eventLoopStream, settings.getNumberOfThreads(), exceptions);

            return new ContextDefaultParallelism(parallelism);
        };
    }
}
