//package com.experimental.threading;
//
//import com.dipasquale.common.ArgumentValidatorUtils;
//import com.dipasquale.threading.ReusableCountDownLatch;
//import com.google.common.collect.ImmutableList;
//
//import java.util.Iterator;
//import java.util.List;
//import java.util.Optional;
//import java.util.concurrent.atomic.AtomicReference;
//import java.util.function.BinaryOperator;
//import java.util.function.Function;
//import java.util.stream.Collectors;
//import java.util.stream.IntStream;
//
//final class IterableExecutorBuilderParameterized<T, TMap> extends IterableExecutorBuilder<TMap> {
//    private final IterableExecutor executor;
//    private final Iterable<T> iterable;
//    private final Function<T, TMap> mapper;
//    private final TMap identity;
//    private final BinaryOperator<TMap> accumulator;
//
//    IterableExecutorBuilderParameterized(final IterableExecutor executor, final Iterable<T> iterable, final Function<T, TMap> mapper, final TMap identity, final BinaryOperator<TMap> accumulator) {
//        super(executor);
//        this.executor = executor;
//        this.iterable = iterable;
//        this.mapper = mapper;
//        this.identity = identity;
//        this.accumulator = accumulator;
//    }
//
//    @Override
//    public <TNextMap> IterableExecutorBuilder<TNextMap> map(final Function<TMap, TNextMap> nextMapper) {
//        Function<T, TNextMap> nextMapperFixed = i -> nextMapper.apply(mapper.apply(i));
//
//        return new IterableExecutorBuilderParameterized<>(executor, iterable, nextMapperFixed, null, null);
//    }
//
//    private Function<T, TMap> getMapper() {
//        return Optional.ofNullable(mapper)
//                .orElse(i -> (TMap) i);
//    }
//
//    @Override
//    IterableExecutorBuilder<TMap> createBuilderForReduction(final TMap identity, final BinaryOperator<TMap> accumulator) {
//        ArgumentValidatorUtils.ensureNotNull(accumulator, "accumulator");
//
//        return new IterableExecutorBuilderParameterized<>(executor, iterable, getMapper(), identity, accumulator);
//    }
//
//    @Override
//    List<IterableExecutorHandler<TMap>> build(final int count) {
//        if (count == 0) {
//            return ImmutableList.of();
//        }
//
//        Iterator<T> iterator = iterable.iterator();
//        Function<T, TMap> mapperFixed = getMapper();
//        AtomicReference<TMap> identityCas = new AtomicReference<>(identity);
//
//        BinaryOperator<TMap> accumulatorFixed = Optional.ofNullable(accumulator)
//                .orElse(getDefaultAccumulator());
//
//        ReusableCountDownLatch waitUntilReducedHandle = new ReusableCountDownLatch(count);
//
//        return IntStream.range(0, count)
//                .mapToObj(i -> new IterableExecutorHandlerDefault<>(executor, iterator, mapperFixed, identityCas, accumulatorFixed, waitUntilReducedHandle))
//                .collect(Collectors.toList());
//    }
//}
