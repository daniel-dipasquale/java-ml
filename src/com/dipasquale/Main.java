package com.dipasquale;

import com.dipasquale.common.IdFactory;
import com.dipasquale.common.RandomSupport;
import com.dipasquale.common.test.JvmWarmup;
import com.dipasquale.common.time.DateTimeSupport;
import com.dipasquale.data.structure.set.DequeSet;
import com.dipasquale.data.structure.set.HashDequeSet;
import com.experimental.cliff.data.structure.map.NonBlockingHashMap;
import com.experimental.metrics.MetricData;
import com.experimental.metrics.MetricDataCollector;
import com.experimental.metrics.MetricDataCollectorConcurrent;
import com.experimental.metrics.MetricDataComparerDefault;
import com.experimental.metrics.MetricDataComparison;
import com.experimental.metrics.MetricDataComparisonResult;
import com.experimental.metrics.MetricDataSelector;
import com.experimental.metrics.MetricDatumRetrieverFactory;
import com.experimental.metrics.MetricDatumRetrieverFactoryDefault;
import com.experimental.metrics.MetricDimensionNameFactory;
import com.experimental.metrics.MetricGroupKeyFactory;
import com.experimental.metrics.MetricKeyDefault;
import com.experimental.stress.DataGenerator;
import com.experimental.stress.DataGeneratorFactory;
import com.experimental.stress.StressTest;
import com.experimental.stress.StressTestCollectionMultiple;
import com.experimental.stress.StressTestMapMultiple;
import com.experimental.stress.StressTestNotification;
import com.experimental.stress.StressTestSetMultiple;
import com.google.common.collect.ImmutableList;

import javax.measure.unit.SI;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public final class Main {
    private static final DateTimeSupport DATE_TIME_SUPPORT_MILLISECONDS = DateTimeSupport.createMilliseconds();
    private static final int TIME_FRAME_IN_MILLISECONDS = 604_800_000;
    private static final IdFactory<Long> THREAD_ID_FACTORY = () -> Thread.currentThread().getId();
    private static final int NUMBER_OF_THREADS = 64;
    private static final MetricDataCollector METRIC_DATA_COLLECTOR = new MetricDataCollectorConcurrent<>(DATE_TIME_SUPPORT_MILLISECONDS, TIME_FRAME_IN_MILLISECONDS, SI.MILLI(SI.SECOND), THREAD_ID_FACTORY, 32, 0.75f, NUMBER_OF_THREADS);
    private static final ExecutorService EXECUTOR_SERVICE = Executors.newFixedThreadPool(NUMBER_OF_THREADS);
    private static final DateTimeSupport DATE_TIME_SUPPORT_NANOSECONDS = DateTimeSupport.createNanoseconds();
    private static final RandomSupport RANDOM_SUPPORT = () -> ThreadLocalRandom.current().nextDouble();

    private static final MetricGroupKeyFactory METRIC_GROUP_KEY_FACTORY = mk -> {
        Iterable<CharSequence> metricNameParts = Arrays.stream(mk.getName().split("\\."))
                .skip(1)
                .map(s -> (CharSequence) s)
                ::iterator;

        String metricName = String.join(".", metricNameParts);

        return new MetricKeyDefault(metricName, mk.getUnit());
    };

    private static final MetricDimensionNameFactory METRIC_DIMENSION_NAME_FACTORY = mk -> mk.getName().split("\\.")[0];
    private static final MetricDatumRetrieverFactory METRIC_DATUM_RETRIEVER_FACTORY = new MetricDatumRetrieverFactoryDefault();
    private static final MetricDataComparerDefault METRIC_DATA_COMPARER = new MetricDataComparerDefault(METRIC_GROUP_KEY_FACTORY, METRIC_DIMENSION_NAME_FACTORY, METRIC_DATUM_RETRIEVER_FACTORY);

    private static final StressTestNotification STRESS_TEST_NOTIFICATION = new StressTestNotification() {
        @Override
        public synchronized void notifyStarting(final StressTest stressTest) {
            System.out.printf("[INFO] starting test '%s' ...%n", stressTest.getName());
        }

        @Override
        public synchronized void notifyCompleted(final StressTest stressTest) {
            System.out.printf("[INFO] test '%s' finished ...%n", stressTest.getName());
        }
    };

    private static final Consumer<MetricDataComparisonResult> COMPARISON_PRINTER = new Consumer<MetricDataComparisonResult>() {
        private String toString(final double n) {
            if (n % 1D != 0) {
                return String.format("%s", n);
            }

            return String.format("%.0f", n);
        }

        @Override
        public void accept(final MetricDataComparisonResult comparisonResult) {
            System.out.printf("category = %d / %s in %s%n", comparisonResult.getDateTime(), comparisonResult.getMetricKey().getName(), comparisonResult.getMetricKey().getUnit());
            System.out.printf("best performant = %s (%s @ %s)%n", comparisonResult.getDimensionName(), comparisonResult.getStatisticName(), toString(comparisonResult.getStatisticValue()));
            comparisonResult.getDegradedEntries().forEach(de -> System.out.printf("compared to = %s (%sx)%n", de.getDimensionName(), toString(de.getRelativeRatio())));
        }
    };

    private static final int RECORD_COUNT = 160_000;
    private static final int ITERATOR_COUNT = 625;
    private static final int REPEAT = 2;

//    private static void todo() {
//        DateTimeSupport dateTimeSupport = System::currentTimeMillis;
//        IdFactory<Long> majorIdFactory = () -> dateTimeSupport.now() / 604_800_000L;
//        IdFactory<Long> threadIdFactory = () -> Thread.currentThread().getId();
//        IdFactoryConcurrentId concurrentIdFactory = new IdFactoryConcurrentId(majorIdFactory, threadIdFactory, 10, 0.75f, 10);
//        TreeBinarySearchMapRedBlackConcurrent<String, String, Long> map = new TreeBinarySearchMapRedBlackConcurrent<>(String::compareTo, concurrentIdFactory);
//    }

    private static StressTest createDataStructureStressTestCollection() {
        DataGenerator<Integer> dataGenerator = DataGeneratorFactory.createNumbersInRandomOrder(RECORD_COUNT, ITERATOR_COUNT);

        return StressTestCollectionMultiple.<Integer>builder()
                .name("CollectionTest")
                .executorService(EXECUTOR_SERVICE)
                .collections(ImmutableList.<Collection<Integer>>builder()
                        .add(Collections.synchronizedSet(new HashSet<>()))
                        .add(DequeSet.createSynchronized(new HashDequeSet<>()))
                        .add(new ConcurrentSkipListSet<>())
                        .add(new ConcurrentLinkedQueue<>())
                        .build())
                .dataGenerator(dataGenerator)
                .dateTimeSupport(DATE_TIME_SUPPORT_NANOSECONDS)
                .repeat(REPEAT)
                .stressTestNotification(STRESS_TEST_NOTIFICATION)
                .build();
    }

    private static StressTest createDataStructureStressTestSet() {
        DataGenerator<Integer> dataGenerator = DataGeneratorFactory.createRandomNumbers(RECORD_COUNT, ITERATOR_COUNT, RANDOM_SUPPORT, 1, 5_000);

        return StressTestSetMultiple.<Integer>builder()
                .name("SetTest")
                .executorService(EXECUTOR_SERVICE)
                .sets(ImmutableList.<Set<Integer>>builder()
                        .add(Collections.synchronizedSet(new HashSet<>()))
                        .add(DequeSet.createSynchronized(new HashDequeSet<>()))
                        .add(new ConcurrentSkipListSet<>())
                        .build())
                .dataGenerator(dataGenerator)
                .dateTimeSupport(DATE_TIME_SUPPORT_NANOSECONDS)
                .repeat(REPEAT)
                .stressTestNotification(STRESS_TEST_NOTIFICATION)
                .build();
    }

    private static StressTest createDataStructureStressTestMap() {
        DataGenerator<Map.Entry<Integer, String>> dataGenerator = DataGeneratorFactory.createRandomEntries(RECORD_COUNT, ITERATOR_COUNT, RANDOM_SUPPORT, 1, 5_000);

        return StressTestMapMultiple.<Integer, String>builder()
                .name("MapTest")
                .executorService(EXECUTOR_SERVICE)
                .maps(ImmutableList.<Map<Integer, String>>builder()
                        .add(Collections.synchronizedNavigableMap(new TreeMap<>(Integer::compareTo)))
                        .add(Collections.synchronizedMap(new HashMap<>(8_192)))
                        .add(new ConcurrentSkipListMap<>(Integer::compareTo))
                        .add(new ConcurrentHashMap<>(8_192, 0.75F, NUMBER_OF_THREADS))
                        .add(new NonBlockingHashMap<>(524_288))
                        .build())
                .dataGenerator(dataGenerator)
                .dateTimeSupport(DATE_TIME_SUPPORT_NANOSECONDS)
                .repeat(REPEAT)
                .stressTestNotification(STRESS_TEST_NOTIFICATION)
                .build();
    }

    public static void main(final String[] args) {
        System.out.println("[INFO] warming up jvm ...");
        JvmWarmup.start(250_000);
        System.out.println("[INFO] jvm warmed up, starting tests ...");
        System.out.println("[INFO] preparing test ... ");

        try {
//            StressTest stressTest = createDataStructureStressTestCollection();
//            StressTest stressTest = createDataStructureStressTestSet();
            StressTest stressTest = createDataStructureStressTestMap();

            System.out.println("[INFO] test setup complete, starting test ... ");
            stressTest.perform(METRIC_DATA_COLLECTOR);
            System.out.println("[INFO] test complete, reporting results ... ");
            System.out.println("===============================================");

            List<MetricData> metrics = METRIC_DATA_COLLECTOR.flush(true);
            MetricDataComparison comparison = METRIC_DATA_COMPARER.compare(metrics);

            List<MetricDataSelector> metricDataSelectors = StreamSupport.stream(stressTest.extractMetricDataSelectors().spliterator(), false)
                    .map(mds -> new MetricDataSelector(METRIC_GROUP_KEY_FACTORY.create(mds.getMetricKey()), mds.getStatisticName()))
                    .distinct()
                    .collect(Collectors.toList());

            for (long dateTime : comparison.getDateTimes()) {
                for (MetricDataSelector metricDataSelector : metricDataSelectors) {
                    MetricDataComparisonResult comparisonResult = comparison.getComparison(dateTime, metricDataSelector);

                    if (comparisonResult != null) {
                        COMPARISON_PRINTER.accept(comparisonResult);
                        System.out.println("===============================================");
                    }
                }
            }

            List<Throwable> exceptions = ImmutableList.copyOf(stressTest.extractExceptions());

            if (!exceptions.isEmpty()) {
                System.out.printf("[INFO] there were some errors found: %n");

                for (Throwable exception : exceptions) {
                    exception.printStackTrace(System.err);
                }
            }
        } finally {
            EXECUTOR_SERVICE.shutdown();
        }
    }
}
