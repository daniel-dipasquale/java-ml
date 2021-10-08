package com.dipasquale.ai.rl.neat.speciation.metric;

import com.dipasquale.common.Record;
import com.dipasquale.metric.MetricDatumFactory;
import com.dipasquale.metric.MetricDatumQueryProjection;
import com.google.common.collect.ImmutableList;

import java.util.List;
import java.util.Map;

public final class MetricsViewer {
    private final Map<Integer, IterationMetrics> all;
    private final GenerationMetricsQueryProjector queryProjector;

    public MetricsViewer(final Map<Integer, IterationMetrics> all, final MetricDatumFactory metricDatumFactory) {
        this.all = all;
        this.queryProjector = new GenerationMetricsQueryProjector("generation", metricDatumFactory);
    }

    private static Iterable<Record<Float, GenerationMetrics>> createGenerationRecords(final Map<Integer, GenerationMetrics> generations) {
        return generations.keySet().stream()
                .sorted(Integer::compare)
                .map(gid -> new Record<>((float) gid, generations.get(gid)))
                ::iterator;
    }

    private Iterable<Record<Float, GenerationMetrics>> createGenerationRecords(final Integer iteration) {
        if (iteration == null) {
            return ImmutableList.of();
        }

        IterationMetrics iterationMetrics = all.get(iteration);

        if (iterationMetrics == null) {
            return ImmutableList.of();
        }

        return createGenerationRecords(iterationMetrics.getGenerations());
    }

    public MetricsResult query(final Integer iteration, final List<MetricDatumQueryProjection> projections) {
        Iterable<Record<Float, GenerationMetrics>> generations = createGenerationRecords(iteration);

        return queryProjector.query(generations, projections);
    }

    public MetricsResult queryLast(final List<MetricDatumQueryProjection> projections) {
        Integer iteration = all.keySet().stream()
                .max(Integer::compare)
                .orElse(null);

        return query(iteration, projections);
    }
}
