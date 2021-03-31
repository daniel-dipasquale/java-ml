package com.dipasquale.threading;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.Iterator;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BinaryOperator;
import java.util.function.Function;

final class IterableExecutorHandlerDefault<T, TMap> implements IterableExecutorHandler<TMap> {
    private final IterableExecutor executor;
    private final Iterator<T> iterator;
    private final Function<T, TMap> mapper;
    private final AtomicReference<TMap> identityCas;
    private final BinaryOperator<TMap> accumulator;
    private boolean processed;
    private final ReusableCountDownLatch waitUntilReducedHandle;
    private final AtomicBoolean stoppedProcessingCas;

    IterableExecutorHandlerDefault(final IterableExecutor executor, final Iterator<T> iterator, final Function<T, TMap> mapper, final AtomicReference<TMap> identity, final BinaryOperator<TMap> accumulator, final ReusableCountDownLatch waitUntilReducedHandle) {
        this.executor = executor;
        this.iterator = iterator;
        this.mapper = mapper;
        this.identityCas = identity;
        this.accumulator = accumulator;
        this.processed = false;
        this.waitUntilReducedHandle = waitUntilReducedHandle;
        this.stoppedProcessingCas = new AtomicBoolean(false);
    }

    private IteratorItem<T> getNext() {
        if (stoppedProcessingCas.get()) {
            return null;
        }

        synchronized (iterator) {
            if (!iterator.hasNext()) {
                return null;
            }

            return new IteratorItem<>(iterator.next());
        }
    }

    @Override
    public void handle(final String name) {
        if (processed) {
            return;
        }

        TMap identityTemporary = identityCas.get();
        boolean processedAtLeastOne = false;

        try {
            for (IteratorItem<T> item = getNext(); item != null; item = getNext()) {
                TMap itemMapped = mapper.apply(item.value);

                identityTemporary = accumulator.apply(itemMapped, identityTemporary);
                processedAtLeastOne = true;
            }
        } finally {
            if (processedAtLeastOne) {
                identityCas.accumulateAndGet(identityTemporary, accumulator);
            }

            if (!stoppedProcessingCas.get()) {
                processed = true;
                waitUntilReducedHandle.countDown();
                executor.remove(name, this);
            } else {
                stoppedProcessingCas.set(false);
                executor.suspend(name);
            }
        }
    }

    @Override
    public TMap awaitReducedValue()
            throws InterruptedException {
        waitUntilReducedHandle.await();

        return identityCas.get();
    }

    @Override
    public void stopProcessing() {
        stoppedProcessingCas.set(true);
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class IteratorItem<T> {
        private final T value;
    }
}
