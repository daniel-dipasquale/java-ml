package com.dipasquale.ai.rl.neat.speciation.metric;

import com.dipasquale.common.Record;
import com.dipasquale.metric.MetricDatum;
import com.dipasquale.metric.MetricDatumFactory;
import com.dipasquale.metric.MetricDatumQueryProjection;
import com.dipasquale.metric.MetricDatumSelector;
import com.dipasquale.metric.MetricsQueryProjector;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;

public final class GenerationMetricsQueryProjector {
    private final MetricsQueryProjector<GenerationMetrics> projector;

    public GenerationMetricsQueryProjector(final String defaultKey, final MetricDatumFactory metricDatumFactory) {
        this.projector = new MetricsQueryProjector<>(defaultKey, createSelectors(metricDatumFactory));
    }

    private static Map<String, MetricDatumSelector<GenerationMetrics>> createSelectors(final MetricDatumFactory metricDatumFactory) {
        SpeciesTopologyMetricsAggregator speciesTopologyMetricsAggregator = new SpeciesTopologyMetricsAggregator(metricDatumFactory);

        return Map.ofEntries(
                Map.entry("organismsInSpecies", new CachedMetricsAggregator<>(new OrganismsInSpeciesMetricsAggregator(metricDatumFactory))),
                Map.entry("speciesTopology.hiddenNodes", speciesTopologyMetricsAggregator.getHiddenNodes()),
                Map.entry("speciesTopology.connections", speciesTopologyMetricsAggregator.getConnections()),
                Map.entry("organismsFitness", new CachedMetricsAggregator<>(new OrganismsFitnessMetricsAggregator(metricDatumFactory))),
                Map.entry("speciesSharedFitness", new CachedMetricsAggregator<>(new SharedFitnessMetricsAggregator(metricDatumFactory))),
                Map.entry("speciesAge", GenerationMetrics::getSpeciesAge),
                Map.entry("speciesStagnationPeriod", GenerationMetrics::getSpeciesStagnationPeriod),
                Map.entry("speciesStagnant", GenerationMetrics::getSpeciesStagnant),
                Map.entry("organismsKilled", new CachedMetricsAggregator<>(new OrganismsKilledMetricsAggregator(metricDatumFactory))),
                Map.entry("speciesExtinct", GenerationMetrics::getSpeciesExtinct)
        );
    }

    public MetricsResult query(final Iterable<Record<Float, GenerationMetrics>> records, final List<MetricDatumQueryProjection> projections) {
        return projector.query(records, projections);
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class OrganismsInSpeciesMetricsAggregator implements MetricDatumSelector<GenerationMetrics> {
        private final MetricDatumFactory metricDatumFactory;

        @Override
        public MetricDatum selectMetricDatum(final GenerationMetrics generationMetrics) {
            MetricDatum metricDatum = metricDatumFactory.create();

            generationMetrics.getOrganismsTopology().values().stream()
                    .map(ot -> ot.getHiddenNodes().getValues().size())
                    .forEach(s -> metricDatum.add((float) s));

            return metricDatum;
        }
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class OrganismsFitnessMetricsAggregator implements MetricDatumSelector<GenerationMetrics> {
        private final MetricDatumFactory metricDatumFactory;

        @Override
        public MetricDatum selectMetricDatum(final GenerationMetrics generationMetrics) {
            MetricDatum metricDatum = metricDatumFactory.create();

            generationMetrics.getFitnessCalculations().stream()
                    .flatMap(fc -> fc.getOrganisms().values().stream())
                    .forEach(metricDatum::merge);

            return metricDatum;
        }
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class SharedFitnessMetricsAggregator implements MetricDatumSelector<GenerationMetrics> {
        private final MetricDatumFactory metricDatumFactory;

        @Override
        public MetricDatum selectMetricDatum(final GenerationMetrics generationMetrics) {
            MetricDatum metricDatum = metricDatumFactory.create();

            generationMetrics.getFitnessCalculations().stream()
                    .map(FitnessMetrics::getShared)
                    .forEach(metricDatum::merge);

            return metricDatum;
        }
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class OrganismsKilledMetricsAggregator implements MetricDatumSelector<GenerationMetrics> {
        private final MetricDatumFactory metricDatumFactory;

        @Override
        public MetricDatum selectMetricDatum(final GenerationMetrics generationMetrics) {
            MetricDatum metricDatum = metricDatumFactory.create();

            generationMetrics.getOrganismsKilled().values().stream()
                    .map(MetricDatum::createReduced)
                    .forEach(metricDatum::merge);

            return metricDatum;
        }
    }
}
