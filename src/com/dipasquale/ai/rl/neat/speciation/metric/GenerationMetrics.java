package com.dipasquale.ai.rl.neat.speciation.metric;

import com.dipasquale.metric.MetricDatum;
import com.dipasquale.metric.MetricDatumFactory;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public final class GenerationMetrics implements Serializable {
    @Serial
    private static final long serialVersionUID = 2802393466929099781L;
    private final Map<String, TopologyMetrics> organismsTopology;
    private final List<FitnessMetrics> fitnessEvaluations;
    private final MetricDatum speciesAge;
    private final MetricDatum speciesStagnationPeriod;
    private final MetricDatum speciesStagnant;
    private final Map<String, MetricDatum> organismsKilled;
    private final MetricDatum speciesExtinct;

    public GenerationMetrics(final MetricDatum speciesAge, final MetricDatum speciesStagnationPeriod, final MetricDatum speciesStagnant, final MetricDatum speciesExtinct) {
        this(new HashMap<>(), new ArrayList<>(), speciesAge, speciesStagnationPeriod, speciesStagnant, new HashMap<>(), speciesExtinct);
    }

    public static GenerationMetrics create(final MetricDatumFactory metricDatumFactory) {
        return new GenerationMetrics(metricDatumFactory.create(), metricDatumFactory.create(), metricDatumFactory.create(), metricDatumFactory.create());
    }

    public GenerationMetrics createCopy() {
        Map<String, TopologyMetrics> copiedOrganismsTopology = organismsTopology.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().createCopy()));

        List<FitnessMetrics> copiedFitnessEvaluations = fitnessEvaluations.stream()
                .map(FitnessMetrics::createCopy)
                .collect(Collectors.toList());

        MetricDatum copiedSpeciesAge = speciesAge.createCopy();
        MetricDatum copiedSpeciesStagnationPeriod = speciesStagnationPeriod.createCopy();
        MetricDatum copiedSpeciesStagnant = speciesStagnant.createCopy();

        Map<String, MetricDatum> copiedOrganismsKilled = organismsKilled.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().createCopy()));

        MetricDatum copiedSpeciesExtinct = speciesExtinct.createCopy();

        return new GenerationMetrics(copiedOrganismsTopology, copiedFitnessEvaluations, copiedSpeciesAge, copiedSpeciesStagnationPeriod, copiedSpeciesStagnant, copiedOrganismsKilled, copiedSpeciesExtinct);
    }

    public void clear() {
        organismsTopology.clear();
        fitnessEvaluations.clear();
        speciesAge.clear();
        speciesStagnationPeriod.clear();
        speciesStagnant.clear();
        speciesExtinct.clear();
    }
}
