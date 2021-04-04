//package com.experimental.threading;
//
//import com.dipasquale.threading.wait.handle.ReusableCountDownLatch;
//import lombok.AccessLevel;
//import lombok.RequiredArgsConstructor;
//
//import java.util.Iterator;
//import java.util.concurrent.atomic.AtomicBoolean;
//import java.util.concurrent.atomic.AtomicReference;
//import java.util.function.BinaryOperator;
//import java.util.function.Function;
//
//final class IterableExecutorHandlerDefault<T, TMap> implements IterableExecutorHandler<TMap> {
//    private final IterableExecutor executor;
//    private final Iterator<T> originalIterator;
//    private Iterator<TMap> mappedIterator;
//    private final Function<T, TMap> mapper;
//    private final AtomicReference<TMap> identityCas;
//    private final BinaryOperator<TMap> accumulator;
//    private boolean processed;
//    private final ReusableCountDownLatch waitUntilReducedHandle;
//    private final AtomicBoolean stoppedProcessingCas;
//
//    IterableExecutorHandlerDefault(final IterableExecutor executor, final Iterator<T> iterator, final Function<T, TMap> mapper, final AtomicReference<TMap> identity, final BinaryOperator<TMap> accumulator, final ReusableCountDownLatch waitUntilReducedHandle) {
//        this.executor = executor;
//        this.originalIterator = iterator;
//        this.mappedIterator = createMappedIterator(iterator, mapper);
//        this.mapper = mapper;
//        this.identityCas = identity;
//        this.accumulator = accumulator;
//        this.processed = false;
//        this.waitUntilReducedHandle = waitUntilReducedHandle;
//        this.stoppedProcessingCas = new AtomicBoolean(false);
//    }
//
//    private static <T, TMap> Iterator<TMap> createMappedIterator(final Iterator<T> iterator, final Function<T, TMap> mapper) {
//        return new Iterator<>() {
//            @Override
//            public boolean hasNext() {
//                return iterator.hasNext();
//            }
//
//            @Override
//            public TMap next() {
//                return mapper.apply(iterator.next());
//            }
//        };
//    }
//
//    private Iterator<TMap> createReplayIterator(final TMap item) {
//        boolean[] consumed = new boolean[]{false};
//
//        return new Iterator<>() {
//            @Override
//            public boolean hasNext() {
//                return !consumed[0] || originalIterator.hasNext();
//            }
//
//            @Override
//            public TMap next() {
//                if (!consumed[0]) {
//                    consumed[0] = true;
//
//                    return item;
//                }
//
//                return mapper.apply(originalIterator.next());
//            }
//        };
//    }
//
//    private IteratorItem<TMap> getNext() {
//        if (stoppedProcessingCas.get()) {
//            return null;
//        }
//
//        synchronized (originalIterator) {
//            if (!mappedIterator.hasNext()) {
//                return null;
//            }
//
//            return new IteratorItem<>(mappedIterator.next());
//        }
//    }
//
//    @Override
//    public void handle(final String name) {
//        if (processed) {
//            return;
//        }
//
//        TMap identityTemporary = identityCas.get();
//        boolean processedAtLeastOne = false;
//
//        try {
//            for (IteratorItem<TMap> item = getNext(); item != null; item = getNext()) {
//                if (!stoppedProcessingCas.get()) {
//                    identityTemporary = accumulator.apply(item.value, identityTemporary);
//                    processedAtLeastOne = true;
//                } else {
//                    mappedIterator = createReplayIterator(item.value);
//                }
//            }
//        } finally {
//            if (processedAtLeastOne) {
//                identityCas.accumulateAndGet(identityTemporary, accumulator);
//            }
//
//            if (!stoppedProcessingCas.get()) {
//                processed = true;
//                waitUntilReducedHandle.countDown();
//                executor.remove(name, this);
//            } else {
//                stoppedProcessingCas.set(false);
//                executor.suspend(name);
//            }
//        }
//    }
//
//    @Override
//    public TMap awaitReducedValue()
//            throws InterruptedException {
//        waitUntilReducedHandle.await();
//
//        return identityCas.get();
//    }
//
//    @Override
//    public void stopProcessing() {
//        stoppedProcessingCas.set(true);
//    }
//
//    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
//    private static final class IteratorItem<T> {
//        private final T value;
//    }
//}
