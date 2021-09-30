package com.dipasquale.ai.rl.neat.core;

import com.dipasquale.ai.rl.neat.speciation.metric.GenerationMetrics;
import com.dipasquale.ai.rl.neat.speciation.metric.IterationMetrics;
import com.dipasquale.ai.rl.neat.speciation.metric.MetricsRecord;
import com.dipasquale.ai.rl.neat.speciation.metric.MetricsResult;
import com.dipasquale.common.Record;
import com.dipasquale.metric.MetricDatumQueryProjection;
import com.google.common.collect.ImmutableList;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class NeatMetricsReporter {
    private static String getCsvHeader(final List<MetricDatumQueryProjection> queryProjections, final MetricsResult result) {
        StringJoiner headerNamesJoiner = new StringJoiner(",");

        headerNamesJoiner.add(result.getDefaultKey());
        queryProjections.forEach(qp -> headerNamesJoiner.add(qp.getId()));

        return headerNamesJoiner.toString();
    }

    private static String format(final float value) {
        if (Float.compare(value, (float) Math.floor(value)) != 0) {
            return Float.toString(value);
        }

        return Integer.toString((int) value);
    }

    private static void addRecordValue(final StringJoiner recordValues, final Float value) {
        if (value != null) {
            recordValues.add(format(value));
        } else {
            recordValues.add("");
        }
    }

    private static String getCsvRecord(final List<MetricDatumQueryProjection> queryProjections, final MetricsResult result, final MetricsRecord record) {
        StringJoiner recordValues = new StringJoiner(",");
        Float defaultValue = record.getValue(result.getDefaultKey());

        addRecordValue(recordValues, defaultValue);

        queryProjections.forEach(qp -> {
            Float value = record.getValue(qp.getId());

            addRecordValue(recordValues, value);
        });

        return recordValues.toString();
    }

    private static Iterable<Record<Float, GenerationMetrics>> createGenerationRecords(final Map<Integer, GenerationMetrics> generations) {
        return generations.keySet().stream()
                .sorted(Integer::compare)
                .map(gid -> new Record<>((float) gid, generations.get(gid)))
                ::iterator;
    }

    private static void displayMetrics(final IterationMetrics iterationMetrics, final List<MetricDatumQueryProjection> queryProjections) {
        MetricsResult result = GenerationMetrics.getQueryProjector().query(createGenerationRecords(iterationMetrics.getGenerations()), queryProjections);
        StringJoiner headerNamesJoiner = new StringJoiner(",");

        headerNamesJoiner.add(result.getDefaultKey());
        queryProjections.forEach(qp -> headerNamesJoiner.add(qp.getId()));

        System.out.println(getCsvHeader(queryProjections, result));

        for (MetricsRecord record : result.getRecords()) {
            System.out.println(getCsvRecord(queryProjections, result, record));
        }
    }

    private static void displayMetrics_Min_Avg_Max(final IterationMetrics iterationMetrics, final String name) {
        List<MetricDatumQueryProjection> queryProjections = ImmutableList.<MetricDatumQueryProjection>builder()
                .add(new MetricDatumQueryProjection(name, "min", "minimum"))
                .add(new MetricDatumQueryProjection(name, "avg", "average"))
                .add(new MetricDatumQueryProjection(name, "max", "maximum"))
                .add(new MetricDatumQueryProjection(name, "p10", "p10"))
                .add(new MetricDatumQueryProjection(name, "p50", "p50"))
                .add(new MetricDatumQueryProjection(name, "p90", "p90"))
                .build();

        displayMetrics(iterationMetrics, queryProjections);
    }

    private static void displayMetrics_Avg_Sum(final IterationMetrics iterationMetrics, final String name) {
        List<MetricDatumQueryProjection> queryProjections = ImmutableList.<MetricDatumQueryProjection>builder()
                .add(new MetricDatumQueryProjection(name, "avg", "percentage"))
                .add(new MetricDatumQueryProjection(name, "sum", "count"))
                .add(new MetricDatumQueryProjection(name, "p10", "p10"))
                .add(new MetricDatumQueryProjection(name, "p50", "p50"))
                .add(new MetricDatumQueryProjection(name, "p90", "p90"))
                .build();

        displayMetrics(iterationMetrics, queryProjections);
    }

    private static void displayMetrics_Count(final IterationMetrics iterationMetrics, final String name, final String countDisplayText) {
        List<MetricDatumQueryProjection> queryProjections = ImmutableList.<MetricDatumQueryProjection>builder()
                .add(new MetricDatumQueryProjection(name, "count", countDisplayText))
                .build();

        displayMetrics(iterationMetrics, queryProjections);
    }

    public static void displayMetrics(final NeatTrainer trainer, final int iteration) {
        IterationMetrics iterationMetrics = trainer.getMetrics().get(iteration);

        System.out.println("========================================");
        System.out.printf("= species count (iteration %d)%n", iteration);
        System.out.println("========================================");
        displayMetrics_Count(iterationMetrics, "speciesAge", "species");
        System.out.println("========================================");
        System.out.printf("= species topology hidden nodes (iteration %d)%n", iteration);
        System.out.println("========================================");
        displayMetrics_Min_Avg_Max(iterationMetrics, "speciesTopology.hiddenNodes");
        System.out.println("========================================");
        System.out.printf("= species topology connections (iteration %d)%n", iteration);
        System.out.println("========================================");
        displayMetrics_Min_Avg_Max(iterationMetrics, "speciesTopology.connections");
        System.out.println("========================================");
        System.out.printf("= species shared fitness (iteration %d)%n", iteration);
        System.out.println("========================================");
        displayMetrics_Min_Avg_Max(iterationMetrics, "speciesSharedFitness");
        System.out.println("========================================");
        System.out.printf("= organisms fitness (iteration %d)%n", iteration);
        System.out.println("========================================");
        displayMetrics_Min_Avg_Max(iterationMetrics, "organismsFitness");
        System.out.println("========================================");
        System.out.printf("= species age (iteration %d)%n", iteration);
        System.out.println("========================================");
        displayMetrics_Min_Avg_Max(iterationMetrics, "speciesAge");
        System.out.println("========================================");
        System.out.printf("= species stagnation period (iteration %d)%n", iteration);
        System.out.println("========================================");
        displayMetrics_Min_Avg_Max(iterationMetrics, "speciesStagnationPeriod");
        System.out.println("========================================");
        System.out.printf("= species is stagnant (iteration %d)%n", iteration);
        System.out.println("========================================");
        displayMetrics_Avg_Sum(iterationMetrics, "speciesStagnant");
    }

    public static void displayMetrics(final NeatTrainer trainer) {
        for (Integer iteration : trainer.getMetrics().keySet()) {
            displayMetrics(trainer, iteration);
        }
    }
}