//package com.experimental.threading;
//
//import com.dipasquale.common.DateTimeSupport;
//import com.dipasquale.common.ExceptionLogger;
//import com.experimental.threading.IterableExecutor;
//import com.google.common.collect.ImmutableList;
//import lombok.AccessLevel;
//import lombok.AllArgsConstructor;
//import lombok.Builder;
//import org.junit.AfterClass;
//import org.junit.Assert;
//import org.junit.Before;
//import org.junit.Test;
//
//import javax.measure.unit.SI;
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.List;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//import java.util.concurrent.atomic.AtomicLong;
//
//public final class IterableExecutorTest {
//    private static final int NUMBER_OF_THREADS = 1;
//    private static final ExecutorService EXECUTOR_SERVICE = Executors.newFixedThreadPool(NUMBER_OF_THREADS);
//    private static final List<Throwable> EXCEPTIONS = Collections.synchronizedList(new ArrayList<>());
//    private static final ExceptionLogger EXCEPTION_LOGGER = EXCEPTIONS::add;
//    private static final AtomicLong CURRENT_DATE_TIME = new AtomicLong();
//    private static final DateTimeSupport DATE_TIME_SUPPORT = DateTimeSupport.create(CURRENT_DATE_TIME::get, SI.MILLI(SI.SECOND));
//
//    @AfterClass
//    public static void afterClass() {
//        EXECUTOR_SERVICE.shutdown();
//    }
//
//    @Before
//    public void before() {
//        EXCEPTIONS.clear();
//    }
//
//    @Test
//    public void TEST_1() {
//        IterableExecutor test = new IterableExecutor(EXECUTOR_SERVICE, NUMBER_OF_THREADS, EXCEPTION_LOGGER, DATE_TIME_SUPPORT);
//
//        try {
//            List<Child> children = ImmutableList.<Child>builder()
//                    .add(new Child(1f))
//                    .add(new Child(2f))
//                    .add(new Child(3f))
//                    .add(new Child(4f))
//                    .add(new Child(5f))
//                    .build();
//
//            float result = test.iterate(children)
//                    .map(c -> c.value + 1)
//                    .awaitReduce(0f, Float::sum);
//
//            Assert.assertEquals(0, Float.compare(20f, result));
//        } catch (InterruptedException e) {
//            Assert.fail("interrupted");
//        } finally {
//            test.shutdown();
//        }
//    }
//
//    @Test
//    public void TEST_2() {
//        IterableExecutor test = new IterableExecutor(EXECUTOR_SERVICE, NUMBER_OF_THREADS, EXCEPTION_LOGGER, DATE_TIME_SUPPORT);
//
//        try {
//            List<Child> children = ImmutableList.<Child>builder()
//                    .add(new Child(1f))
//                    .add(new Child(2f))
//                    .add(new Child(3f))
//                    .add(new Child(4f))
//                    .add(new Child(5f))
//                    .build();
//
//            float result = test.iterate(children)
//                    .map(c -> {
//                        c.value = c.value + 1f;
//
//                        return c;
//                    })
//                    .map(c -> c.value)
//                    .awaitReduce(0f, Float::sum);
//
//            Assert.assertEquals(0, Float.compare(20f, result));
//        } catch (InterruptedException e) {
//            Assert.fail("interrupted");
//        } finally {
//            test.shutdown();
//        }
//    }
//
//    @Test
//    public void TEST_3() {
//        IterableExecutor test = new IterableExecutor(EXECUTOR_SERVICE, NUMBER_OF_THREADS, EXCEPTION_LOGGER, DATE_TIME_SUPPORT);
//
//        try {
//            Population population = Population.builder()
//                    .parents(ImmutableList.<Parent>builder()
//                            .add(Parent.builder()
//                                    .children(ImmutableList.<Child>builder()
//                                            .add(new Child(1f))
//                                            .add(new Child(2f))
//                                            .add(new Child(3f))
//                                            .add(new Child(4f))
//                                            .add(new Child(5f))
//                                            .build())
//                                    .build())
//                            .add(Parent.builder()
//                                    .children(ImmutableList.<Child>builder()
//                                            .add(new Child(6f))
//                                            .add(new Child(7f))
//                                            .build())
//                                    .build())
//                            .build())
//                    .build();
//
//            population.determineMinimumAsync(test);
//            test.awaitUntilDone();
//
//            Assert.assertEquals(0, Float.compare(13f, population.value));
//        } catch (InterruptedException e) {
//            Assert.fail("interrupted");
//        } finally {
//            test.shutdown();
//        }
//    }
//
//    @AllArgsConstructor(access = AccessLevel.PACKAGE)
//    private static final class Child {
//        private float value;
//    }
//
//    @AllArgsConstructor(access = AccessLevel.PACKAGE)
//    @Builder(access = AccessLevel.PACKAGE)
//    private static final class Parent {
//        private final List<Child> children;
//        private volatile float value = 0f;
//
//        public Parent determineSumAsync(final IterableExecutor executor) {
//            executor.iterate(children)
//                    .map(c -> c.value)
//                    .reduce(0f, (a, b) -> {
//                        synchronized (children) {
//                            return value = a + b;
//                        }
//                    });
//
//            return this;
//        }
//
//        public float determineSum(final IterableExecutor executor)
//                throws InterruptedException {
//            return value = executor.iterate(children)
//                    .map(c -> c.value)
//                    .awaitReduce(0f, Float::sum);
//        }
//    }
//
//    @AllArgsConstructor(access = AccessLevel.PACKAGE)
//    @Builder(access = AccessLevel.PACKAGE)
//    private static final class Population {
//        private final List<Parent> parents;
//        private volatile float value = 0f;
//
//        public Population determineMinimumAsync(final IterableExecutor executor) {
//            executor.iterate(parents)
//                    .map(p -> p.determineSumAsync(executor))
//                    .map(p -> p.value)
//                    .reduce(Float.MAX_VALUE, (a, b) -> {
//                        synchronized (parents) {
//                            return value = Math.min(a, b);
//                        }
//                    });
//
//            return this;
//        }
//    }
//}
