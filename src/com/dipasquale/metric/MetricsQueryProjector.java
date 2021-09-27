package com.dipasquale.metric;

import com.dipasquale.ai.rl.neat.speciation.metric.MetricsRecord;
import com.dipasquale.ai.rl.neat.speciation.metric.MetricsResult;
import com.dipasquale.common.Record;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public final class MetricsQueryProjector<TMetricGroup> {
    private final String defaultKey;
    private final Map<String, MetricDatumSelector<TMetricGroup>> selectors;

    private Float queryValue(final TMetricGroup metricGroup, final MetricDatumQueryProjection projection) {
        MetricDatum metricDatum = selectors.get(projection.getKey()).selectMetricDatum(metricGroup);

        if (metricDatum == null) {
            return null;
        }

        return MetricDatumValueSelection.create(metricDatum, projection.getStatistic()).getValue();
    }

    public MetricsResult query(final Iterable<Record<Float, TMetricGroup>> records, final List<MetricDatumQueryProjection> projections) {
        List<MetricsRecord> results = new ArrayList<>();

        for (Record<Float, TMetricGroup> record : records) {
            MetricsRecord metricRecord = new MetricsRecord();

            for (MetricDatumQueryProjection projection : projections) {
                metricRecord.setValue(projection.getId(), queryValue(record.getValue(), projection));
            }

            metricRecord.setValue(defaultKey, record.getKey());
            results.add(metricRecord);
        }

        return new MetricsResult(defaultKey, results);
    }
}
