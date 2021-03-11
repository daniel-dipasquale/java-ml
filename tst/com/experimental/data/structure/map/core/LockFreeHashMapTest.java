//package com.experimental.data.structure.map.core;
//
//import org.junit.Assert;
//import org.junit.Ignore;
//import org.junit.Test;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//import java.util.concurrent.ConcurrentHashMap;
//import java.util.concurrent.ExecutionException;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//import java.util.concurrent.Future;
//
//public final class LockFreeHashMapTest {
//    @Test
//    public void TEST_1() {
//        LockFreeHashMap<String, String> map = new LockFreeHashMap<>();
//
//        Assert.assertEquals(0, map.size());
//        Assert.assertNull(map.get("key-1"));
//        Assert.assertNull(map.put("key-1", "value-1"));
//        Assert.assertEquals(1, map.size());
//        Assert.assertEquals("value-1", map.get("key-1"));
//        Assert.assertEquals("value-1", map.put("key-1", "value-2"));
//    }
//
//    private static void putEntries(final Map<String, String> map, final int size)
//            throws ExecutionException, InterruptedException {
//        ExecutorService executorService = Executors.newCachedThreadPool();
//        List<Future<?>> futures = new ArrayList<>();
//
//        try {
//            for (int i = 0, c = Runtime.getRuntime().availableProcessors(); i < c; i++) {
//                Future<?> future = executorService.submit(() -> {
//                    for (int index = 0; index < size; index++) {
//                        String key = String.format("key-%d", index);
//                        String value = String.format("value-%d", index);
//
//                        // Assert.assertNull();
//                        map.put(key, value);
//                    }
//                });
//
//                futures.add(future);
//            }
//
//            for (Future<?> future : futures) {
//                future.get();
//            }
//        } finally {
//            executorService.shutdown();
//        }
//    }
//
//    @Test
//    @Ignore
//    public void TEST_2()
//            throws ExecutionException, InterruptedException {
//        putEntries(new LockFreeHashMap<>(), 1_000_000);
//    }
//
//    @Test
//    @Ignore
//    public void TEST_3()
//            throws ExecutionException, InterruptedException {
//        putEntries(new ConcurrentHashMap<>(), 1_000_000);
//    }
//}
