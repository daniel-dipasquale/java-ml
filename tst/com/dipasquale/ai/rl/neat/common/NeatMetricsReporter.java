package com.dipasquale.ai.rl.neat.common;

import com.dipasquale.ai.rl.neat.NeatTrainer;
import com.dipasquale.ai.rl.neat.speciation.metric.MetricsRecord;
import com.dipasquale.ai.rl.neat.speciation.metric.MetricsResult;
import com.dipasquale.ai.rl.neat.speciation.metric.MetricsViewer;
import com.dipasquale.metric.MetricDatumQueryProjection;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.StringJoiner;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class NeatMetricsReporter {
    private static String getCsvHeader(final List<MetricDatumQueryProjection> queryProjections, final MetricsResult result) {
        StringJoiner headerNamesJoiner = new StringJoiner(",");

        headerNamesJoiner.add(result.getDefaultKey());
        queryProjections.forEach(queryProjection -> headerNamesJoiner.add(queryProjection.getId()));

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

        queryProjections.forEach(queryProjection -> {
            Float value = record.getValue(queryProjection.getId());

            addRecordValue(recordValues, value);
        });

        return recordValues.toString();
    }

    private static void displayMetrics(final MetricsResult result) {
        StringJoiner headerNamesJoiner = new StringJoiner(",");

        headerNamesJoiner.add(result.getDefaultKey());
        result.getProjections().forEach(queryProjection -> headerNamesJoiner.add(queryProjection.getId()));

        System.out.println(getCsvHeader(result.getProjections(), result));

        for (MetricsRecord record : result.getRecords()) {
            System.out.println(getCsvRecord(result.getProjections(), result, record));
        }
    }

    private static void displayMetrics_Count(final MetricsViewer metricsViewer, final String name, final String countDisplayText) {
        List<MetricDatumQueryProjection> queryProjections = List.of(new MetricDatumQueryProjection(name, "count", String.format("1. %s", countDisplayText)));

        displayMetrics(metricsViewer.queryLast(queryProjections));
    }

    private static void displayMetrics_Min_Avg_Max(final MetricsViewer metricsViewer, final String name) {
        List<MetricDatumQueryProjection> queryProjections = List.of(
                new MetricDatumQueryProjection(name, "min", "1. minimum"),
                new MetricDatumQueryProjection(name, "avg", "2. average"),
                new MetricDatumQueryProjection(name, "max", "3. maximum"),
                new MetricDatumQueryProjection(name, "p10", "4. p10"),
                new MetricDatumQueryProjection(name, "p50", "5. p50"),
                new MetricDatumQueryProjection(name, "p90", "6. p90")
        );

        displayMetrics(metricsViewer.queryLast(queryProjections));
    }

    private static void displayMetrics_Avg(final MetricsViewer metricsViewer, final String name, final String averageDisplayText) {
        List<MetricDatumQueryProjection> queryProjections = List.of(new MetricDatumQueryProjection(name, "avg", String.format("1. %s", averageDisplayText)));

        displayMetrics(metricsViewer.queryLast(queryProjections));
    }

    private static void displayMetrics_Sum(final MetricsViewer metricsViewer, final String name) {
        List<MetricDatumQueryProjection> queryProjections = List.of(new MetricDatumQueryProjection(name, "sum", "1. count"));

        displayMetrics(metricsViewer.queryLast(queryProjections));
    }

    public static void displayMetrics(final MetricsViewer metricsViewer) {
        System.out.println("========================================");
        System.out.println("= species count");
        System.out.println("========================================");
        displayMetrics_Count(metricsViewer, "speciesAge", "species");
        System.out.println("========================================");
        System.out.println("= organisms per species");
        System.out.println("========================================");
        displayMetrics_Min_Avg_Max(metricsViewer, "organismsInSpecies");
        System.out.println("========================================");
        System.out.println("= species topology hidden nodes");
        System.out.println("========================================");
        displayMetrics_Min_Avg_Max(metricsViewer, "speciesTopology.hiddenNodes");
        System.out.println("========================================");
        System.out.println("= species topology connections");
        System.out.println("========================================");
        displayMetrics_Min_Avg_Max(metricsViewer, "speciesTopology.connections");
        System.out.println("========================================");
        System.out.println("= species shared fitness");
        System.out.println("========================================");
        displayMetrics_Min_Avg_Max(metricsViewer, "speciesSharedFitness");
        System.out.println("========================================");
        System.out.println("= organisms fitness");
        System.out.println("========================================");
        displayMetrics_Min_Avg_Max(metricsViewer, "organismsFitness");
        System.out.println("========================================");
        System.out.println("= species age");
        System.out.println("========================================");
        displayMetrics_Min_Avg_Max(metricsViewer, "speciesAge");
        System.out.println("========================================");
        System.out.println("= species stagnation period");
        System.out.println("========================================");
        displayMetrics_Min_Avg_Max(metricsViewer, "speciesStagnationPeriod");
        System.out.println("========================================");
        System.out.println("= species is stagnant (count)");
        System.out.println("========================================");
        displayMetrics_Sum(metricsViewer, "speciesStagnant");
        System.out.println("========================================");
        System.out.println("= species is stagnant (average)");
        System.out.println("========================================");
        displayMetrics_Avg(metricsViewer, "speciesStagnant", "percentage");
        System.out.println("========================================");
        System.out.println("= organisms killed (total)");
        System.out.println("========================================");
        displayMetrics_Sum(metricsViewer, "organismsKilled");
        System.out.println("========================================");
        System.out.println("= organisms killed per species");
        System.out.println("========================================");
        displayMetrics_Min_Avg_Max(metricsViewer, "organismsKilled");
        System.out.println("========================================");
        System.out.println("= species extinct (count)");
        System.out.println("========================================");
        displayMetrics_Sum(metricsViewer, "speciesExtinct");
        System.out.println("========================================");
        System.out.println("= species extinct (average)");
        System.out.println("========================================");
        displayMetrics_Avg(metricsViewer, "speciesExtinct", "percentage");
    }

    public static void displayMetrics(final NeatTrainer trainer) {
        displayMetrics(trainer.getState().createMetricsViewer());
    }
}
