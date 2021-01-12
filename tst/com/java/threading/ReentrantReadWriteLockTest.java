package com.java.threading;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Collections;
import java.util.Formatter;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public final class ReentrantReadWriteLockTest {
    private static StringBuilder STRING_BUILDER;

    private static final Formatter FORMATTER = new Formatter(new Appendable() {
        @Override
        public Appendable append(final CharSequence csq) {
            STRING_BUILDER.append(csq);

            return this;
        }

        @Override
        public Appendable append(final CharSequence csq, final int start, final int end) {
            STRING_BUILDER.append(csq.subSequence(start, end));

            return this;
        }

        @Override
        public Appendable append(final char c) {
            STRING_BUILDER.append(c);

            return this;
        }
    });

    private static synchronized void printf(final String format, final Object... args) {
        FORMATTER.format(Locale.getDefault(), format, args);
    }

    private static Future<?> submitTask(final ExecutorService executorService, final Lock lock, final String name, final long wait) {
        return executorService.submit(() -> {
            lock.lock();

            try {
                printf("%s locked%n", name);

                if (wait > 0L) {
                    try {
                        Thread.sleep(wait);
                    } catch (InterruptedException e) {
                        printf("%s interrupted before unlocking: %s%n", name, e);
                    }
                }
            } finally {
                printf("%s unlocking%n", name);
                lock.unlock();
            }
        });
    }

    @Before
    public void before() {
        STRING_BUILDER = new StringBuilder();
    }

    @Test
    @Ignore
    public void TEST_1() {
        ExecutorService executorService = Executors.newFixedThreadPool(9);
        ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock(true);
        List<Future<?>> tasks = Collections.synchronizedList(new LinkedList<>());
        CountDownLatch countDownLatch = new CountDownLatch(1);

        tasks.add(executorService.submit(() -> {
            readWriteLock.readLock().lock();

            try {
                printf("reader-1 locked%n");
                tasks.add(submitTask(executorService, readWriteLock.writeLock(), "write-1", 0L));
                tasks.add(submitTask(executorService, readWriteLock.readLock(), "reader-2", 2L));
                tasks.add(submitTask(executorService, readWriteLock.readLock(), "reader-3", 2L));
                tasks.add(submitTask(executorService, readWriteLock.writeLock(), "write-2", 0L));

                try {
                    Thread.sleep(2L);
                } catch (InterruptedException e) {
                    printf("reader-1 interrupted before unlocking: %s%n", e);
                }

                tasks.add(submitTask(executorService, readWriteLock.readLock(), "reader-4", 2L));
                tasks.add(submitTask(executorService, readWriteLock.writeLock(), "write-3", 1L));
                tasks.add(submitTask(executorService, readWriteLock.readLock(), "reader-5", 2L));
                countDownLatch.countDown();
            } finally {
                printf("reader-1 unlocking%n");
                readWriteLock.readLock().unlock();
            }
        }));

        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            printf("countDownLatch.await() exception: %s%n", e);
        }

        for (Future<?> task : tasks) {
            try {
                task.get();
            } catch (InterruptedException | ExecutionException e) {
                printf("task.get() exception: %s%n", e);
            }
        }

        executorService.shutdown();

        try {
            if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            printf("executorService.awaitTermination() exception: %s%n", e);
            Thread.currentThread().interrupt();
        }
    }
}
