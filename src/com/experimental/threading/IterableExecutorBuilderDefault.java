//package com.experimental.threading;
//
//import com.dipasquale.common.ArgumentValidatorUtils;
//import com.dipasquale.threading.wait.handle.ReusableCountDownLatch;
//import com.google.common.collect.ImmutableList;
//
//import java.util.Iterator;
//import java.util.List;
//import java.util.concurrent.atomic.AtomicReference;
//import java.util.function.BinaryOperator;
//import java.util.function.Function;
//import java.util.stream.Collectors;
//import java.util.stream.IntStream;
//
//final class IterableExecutorBuilderDefault<T> extends IterableExecutorBuilder<T> {
//    private final IterableExecutor executor;
//    private final Iterable<T> iterable;
//
//    IterableExecutorBuilderDefault(final IterableExecutor executor, final Iterable<T> iterable) {
//        super(executor);
//        this.executor = executor;
//        this.iterable = iterable;
//    }
//
//    @Override
//    public <TMap> IterableExecutorBuilder<TMap> map(final Function<T, TMap> mapper) {
//        return new IterableExecutorBuilderParameterized<>(executor, iterable, mapper, null, null);
//    }
//
//    @Override
//    IterableExecutorBuilder<T> createBuilderForReduction(final T identity, final BinaryOperator<T> accumulator) {
//        ArgumentValidatorUtils.ensureNotNull(accumulator, "accumulator");
//
//        return new IterableExecutorBuilderParameterized<>(executor, iterable, getDefaultMapper(), identity, accumulator);
//    }
//
//    @Override
//    List<IterableExecutorHandler<T>> build(final int count) {
//        if (count == 0) {
//            return ImmutableList.of();
//        }
//
//        Iterator<T> iterator = iterable.iterator();
//        Function<T, T> mapper = getDefaultMapper();
//        AtomicReference<T> identity = new AtomicReference<>(null);
//        BinaryOperator<T> accumulator = getDefaultAccumulator();
//        ReusableCountDownLatch waitUntilReducedHandle = new ReusableCountDownLatch(count);
//
//        return IntStream.range(0, count)
//                .mapToObj(i -> new IterableExecutorHandlerDefault<>(executor, iterator, mapper, identity, accumulator, waitUntilReducedHandle))
//                .collect(Collectors.toList());
//    }
//}
