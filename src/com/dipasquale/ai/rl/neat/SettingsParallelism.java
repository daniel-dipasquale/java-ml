package com.dipasquale.ai.rl.neat;

import com.dipasquale.ai.common.SequentialIdFactory;
import com.dipasquale.ai.rl.neat.context.Context;
import com.dipasquale.ai.rl.neat.context.ContextDefaultComponentFactory;
import com.dipasquale.ai.rl.neat.context.ContextDefaultParallelism;
import com.dipasquale.common.RandomSupportFloat;
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
    private static final RandomSupportFloat RANDOM_SUPPORT_UNIFORM = RandomSupportFloat.create();
    private static final RandomSupportFloat RANDOM_SUPPORT_MEAN_DISTRIBUTED = RandomSupportFloat.createMeanDistribution();
    private static final RandomSupportFloat RANDOM_SUPPORT_UNIFORM_CONCURRENT = RandomSupportFloat.createConcurrent();
    private static final RandomSupportFloat RANDOM_SUPPORT_MEAN_DISTRIBUTED_CONCURRENT = RandomSupportFloat.createMeanDistributionConcurrent();
    @Builder.Default
    private final ExecutorService executorService = null;
    @Builder.Default
    private final int numberOfThreads = 1;

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

    RandomSupportFloat getRandomSupport(final SettingsRandomType type) {
        if (!isEnabled()) {
            return switch (type) {
                case UNIFORM -> RANDOM_SUPPORT_UNIFORM;

                case MEAN_DISTRIBUTED -> RANDOM_SUPPORT_MEAN_DISTRIBUTED;
            };
        }

        return switch (type) {
            case UNIFORM -> RANDOM_SUPPORT_UNIFORM_CONCURRENT;

            case MEAN_DISTRIBUTED -> RANDOM_SUPPORT_MEAN_DISTRIBUTED_CONCURRENT;
        };
    }

    SequentialIdFactory createSequentialIdFactory(final String name, final SequentialIdFactory sequentialIdFactory) {
        if (!isEnabled()) {
            return new SequentialIdFactoryDefault(name, sequentialIdFactory);
        }

        return new SequentialIdFactorySynchronized(name, sequentialIdFactory);
    }
}
