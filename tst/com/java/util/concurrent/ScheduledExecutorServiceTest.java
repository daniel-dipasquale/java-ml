package com.java.util.concurrent;

import org.junit.Ignore;
import org.junit.Test;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public final class ScheduledExecutorServiceTest {
    private static Runnable createCommand(final String id, final Map<String, AtomicLong> dateTimesPerCommand, final Map<Long, AtomicLong> dateTimesPerThread, final Map<Long, AtomicInteger> timesPerThread, final long sleep) {
        return () -> {
            Thread thread = Thread.currentThread();
            long currentDateTime = System.currentTimeMillis();

            long previousDateTimePerCommand = dateTimesPerCommand.computeIfAbsent(id, k -> new AtomicLong(currentDateTime))
                    .getAndSet(currentDateTime);

            long previousDateTimePerThread = dateTimesPerThread.computeIfAbsent(thread.getId(), tid -> new AtomicLong(currentDateTime))
                    .getAndSet(currentDateTime);

            long previousDateTimePerRun = dateTimesPerThread.computeIfAbsent(-1L, tid -> new AtomicLong(currentDateTime))
                    .getAndSet(currentDateTime);

            timesPerThread.computeIfAbsent(thread.getId(), tid -> new AtomicInteger(0)).incrementAndGet();
            timesPerThread.computeIfAbsent(-1L, tid -> new AtomicInteger(0)).incrementAndGet();

            synchronized (System.out) {
                System.out.printf("(command: %s, %d) (thread: %s, %d) (run: %d)%n", id, currentDateTime - previousDateTimePerCommand, thread.getName(), currentDateTime - previousDateTimePerThread, currentDateTime - previousDateTimePerRun);
            }

            try {
                Thread.sleep(sleep);
            } catch (InterruptedException e) {
                synchronized (System.out) {
                    System.out.printf("error: %s%n", e.getMessage());
                }
            }
        };
    }

    @Test
    @Ignore
    public void TEST_1() {
        long initialDelay = 0L;
        long delay = 100L;
        long sleepPerCommand = 30L;
        long wait = 2_000L;
        int threads = 2;
        int submissions = 50;
        long sleepPerSubmission = 13L;
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(threads);
        Map<String, AtomicLong> dateTimesPerCommand = new ConcurrentHashMap<>();
        Map<Long, AtomicLong> dateTimesPerThread = new ConcurrentHashMap<>();
        Map<Long, AtomicInteger> timesPerThread = new ConcurrentHashMap<>();

        for (int i = 0; i < submissions; i++) {
            try {
                Thread.sleep(sleepPerSubmission);
            } catch (InterruptedException e) {
                synchronized (System.out) {
                    System.out.printf("exception per submission: %s%n", e.getMessage());
                }
            }

            scheduledExecutorService.scheduleWithFixedDelay(createCommand(Integer.toString(i), dateTimesPerCommand, dateTimesPerThread, timesPerThread, sleepPerCommand), initialDelay, delay, TimeUnit.MILLISECONDS);
//            scheduledExecutorService.scheduleAtFixedRate(createCommand(Integer.toString(i), dateTimes, times, sleep), initialDelay, delay, TimeUnit.MILLISECONDS);
        }

        try {
            new CountDownLatch(1).await(wait, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            synchronized (System.out) {
                System.out.printf("done for now%n");
            }
        }

        scheduledExecutorService.shutdown();

        synchronized (System.out) {
            System.out.printf("times: %s%n", timesPerThread);
        }
    }
}
