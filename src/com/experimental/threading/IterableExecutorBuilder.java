//package com.experimental.threading;
//
//import lombok.AccessLevel;
//import lombok.AllArgsConstructor;
//
//import java.util.List;
//import java.util.function.BinaryOperator;
//import java.util.function.Function;
//
//@AllArgsConstructor(access = AccessLevel.PACKAGE)
//public abstract class IterableExecutorBuilder<T> {
//    private static final Function<Object, Object> DEFAULT_MAPPER = i -> i;
//    private static final BinaryOperator<Object> DEFAULT_ACCUMULATOR = (a, b) -> a;
//    private final IterableExecutor executor;
//
//    public abstract <TMap> IterableExecutorBuilder<TMap> map(Function<T, TMap> mapper);
//
//    abstract IterableExecutorBuilder<T> createBuilderForReduction(T identity, BinaryOperator<T> accumulator);
//
//    public T awaitReduce(final T identity, final BinaryOperator<T> accumulator)
//            throws InterruptedException {
//        IterableExecutorBuilder<T> builder = createBuilderForReduction(identity, accumulator);
//        List<IterableExecutorHandler<T>> handlers = executor.push(builder);
//
//        if (handlers.isEmpty()) {
//            return null;
//        }
//
//        return handlers.get(0).awaitReducedValue();
//    }
//
//    public void reduce(final T identity, final BinaryOperator<T> accumulator) {
//        IterableExecutorBuilder<T> builder = createBuilderForReduction(identity, accumulator);
//
//        executor.push(builder);
//    }
//
//    static <T> Function<T, T> getDefaultMapper() {
//        return (Function<T, T>) DEFAULT_MAPPER;
//    }
//
//    static <T> BinaryOperator<T> getDefaultAccumulator() {
//        return (BinaryOperator<T>) DEFAULT_ACCUMULATOR;
//    }
//
//    abstract List<IterableExecutorHandler<T>> build(final int count);
//}
