package com.dipasquale.ai.rl.neat.speciation.metric;

import com.dipasquale.metric.MetricDatum;
import com.dipasquale.metric.MetricDatumFactory;
import com.dipasquale.metric.MetricDatumSelector;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class SpeciesTopologyMetricsAggregator {
    private final MetricDatumFactory metricDatumFactory;
    private GenerationMetrics cachedGenerationMetrics = null;
    private TopologyMetrics cachedTopologyMetrics = null;
    @Getter
    private final MetricDatumSelector<GenerationMetrics> hiddenNodes = new SingleMetricDatumSelector(this, TopologyMetrics::getHiddenNodeGenes);
    @Getter
    private final MetricDatumSelector<GenerationMetrics> connections = new SingleMetricDatumSelector(this, TopologyMetrics::getConnectionGenes);

    private TopologyMetrics createTopologyMetrics(final GenerationMetrics generationMetrics) {
        TopologyMetrics topologyMetrics = new TopologyMetrics(metricDatumFactory.create(), metricDatumFactory.create());

        generationMetrics.getOrganismsTopology().values().forEach(topologyMetrics::merge);

        return topologyMetrics;
    }

    private TopologyMetrics ensureInitialized(final GenerationMetrics generationMetrics) {
        if (cachedGenerationMetrics != generationMetrics) {
            cachedGenerationMetrics = generationMetrics;
            cachedTopologyMetrics = createTopologyMetrics(generationMetrics);
        }

        return cachedTopologyMetrics;
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class SingleMetricDatumSelector implements MetricDatumSelector<GenerationMetrics> {
        private final SpeciesTopologyMetricsAggregator speciesTopologyMetricsAggregator;
        private final MetricDatumSelector<TopologyMetrics> metricDatumSelector;

        @Override
        public MetricDatum selectMetricDatum(final GenerationMetrics generationMetrics) {
            TopologyMetrics topologyMetrics = speciesTopologyMetricsAggregator.ensureInitialized(generationMetrics);

            return metricDatumSelector.selectMetricDatum(topologyMetrics);
        }
    }
}
