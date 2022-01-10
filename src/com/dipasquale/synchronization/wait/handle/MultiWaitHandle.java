package com.dipasquale.synchronization.wait.handle;

import com.dipasquale.common.time.DateTimeSupport;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public final class MultiWaitHandle implements WaitHandle {
    private final List<? extends WaitHandle> waitHandles;
    private final DateTimeSupport dateTimeSupport;
    private final WaitHandleStrategy waitHandleStrategy;

    public MultiWaitHandle(final List<? extends WaitHandle> waitHandles, final DateTimeSupport dateTimeSupport) {
        this(waitHandles, dateTimeSupport, await -> await == 1);
    }

    private static <TWaitHandle extends WaitHandle, TItem> List<TWaitHandle> adapt(final List<TItem> items, final WaitHandleFactory<TItem, TWaitHandle> waitHandlerFactory) {
        return items.stream()
                .map(waitHandlerFactory::create)
                .collect(Collectors.toList());
    }

    public static <TWaitHandle extends WaitHandle, TItem> MultiWaitHandle create(final List<TItem> items, final WaitHandleFactory<TItem, TWaitHandle> waitHandlerFactory, final DateTimeSupport dateTimeSupport, final WaitHandleStrategy waitHandleStrategy) {
        List<TWaitHandle> waitHandles = adapt(items, waitHandlerFactory);

        return new MultiWaitHandle(waitHandles, dateTimeSupport, waitHandleStrategy);
    }

    public static <TWaitHandle extends WaitHandle, TItem> MultiWaitHandle create(final List<TItem> items, final WaitHandleFactory<TItem, TWaitHandle> waitHandlerFactory, final DateTimeSupport dateTimeSupport) {
        List<TWaitHandle> waitHandles = adapt(items, waitHandlerFactory);

        return new MultiWaitHandle(waitHandles, dateTimeSupport);
    }

    @Override
    public void await()
            throws InterruptedException {
        for (int attempt = 0; waitHandleStrategy.shouldAwait(++attempt); ) {
            for (WaitHandle waitHandle : waitHandles) {
                waitHandle.await();
            }
        }
    }

    @Override
    public boolean await(final long timeout, final TimeUnit timeUnit)
            throws InterruptedException {
        boolean acquired = true;
        long offsetDateTime = dateTimeSupport.now();
        long timeoutRemaining = dateTimeSupport.timeUnit().convert(timeout, timeUnit);

        for (int attempt = 0; acquired && timeoutRemaining > 0L && waitHandleStrategy.shouldAwait(++attempt); ) {
            for (int i = 0, c = waitHandles.size(); i < c && acquired && timeoutRemaining > 0L; i++) {
                acquired = waitHandles.get(i).await(timeoutRemaining, dateTimeSupport.timeUnit());

                if (acquired) {
                    long currentDateTime = dateTimeSupport.now();

                    timeoutRemaining -= currentDateTime - offsetDateTime;
                    offsetDateTime = currentDateTime;
                }
            }
        }

        return acquired;
    }
}
