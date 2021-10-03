package com.dipasquale.ai.rl.neat.speciation.metric;

import com.dipasquale.common.factory.data.structure.map.MapFactory;
import com.dipasquale.metric.MetricDatum;
import com.dipasquale.metric.MetricDatumSelector;
import com.dipasquale.metric.MetricsQueryProjector;
import com.google.common.collect.ImmutableMap;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Getter
public final class GenerationMetrics implements Serializable {
    @Serial
    private static final long serialVersionUID = 2802393466929099781L;

    private static final Map<String, MetricDatumSelector<GenerationMetrics>> SELECTORS = ImmutableMap.<String, MetricDatumSelector<GenerationMetrics>>builder()
            .put("speciesTopology.hiddenNodes", gm -> gm.speciesTopology.getHiddenNodes())
            .put("speciesTopology.connections", gm -> gm.speciesTopology.getConnections())
            .put("organismsFitness", GenerationMetrics::getSpeciesAllFitness)
            .put("speciesSharedFitness", GenerationMetrics::getSpeciesSharedFitness)
            .put("speciesAge", GenerationMetrics::getSpeciesAge)
            .put("speciesStagnationPeriod", GenerationMetrics::getSpeciesStagnationPeriod)
            .put("speciesStagnant", GenerationMetrics::getSpeciesStagnant)
            .build();

    private static final MetricsQueryProjector<GenerationMetrics> QUERY_PROJECTOR = new MetricsQueryProjector<>("generation", SELECTORS);
    private final Map<String, TopologyMetrics> organismsTopology;
    private final TopologyMetrics speciesTopology;
    private final List<FitnessMetrics> organismsFitness;
    private final MetricDatum speciesAllFitness;
    private final MetricDatum speciesSharedFitness;
    private final MetricDatum speciesAge;
    private final MetricDatum speciesStagnationPeriod;
    private final MetricDatum speciesStagnant;

    public static MetricsQueryProjector<GenerationMetrics> getQueryProjector() {
        return QUERY_PROJECTOR;
    }

    public GenerationMetrics createCopy(final MapFactory mapFactory) {
        Map<String, TopologyMetrics> organismsTopologyCopied = organismsTopology.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().createCopy()));

        TopologyMetrics speciesTopologyCopied = speciesTopology.createCopy();

        List<FitnessMetrics> organismsFitnessCopied = organismsFitness.stream()
                .map(fm -> fm.createCopy(mapFactory))
                .collect(Collectors.toList());

        MetricDatum speciesAllFitnessCopied = speciesAllFitness.createCopy();
        MetricDatum speciesSharedFitnessCopied = speciesSharedFitness.createCopy();
        MetricDatum speciesAgeCopied = speciesAge.createCopy();
        MetricDatum speciesStagnationPeriodCopied = speciesStagnationPeriod.createCopy();
        MetricDatum speciesStagnantCopied = speciesStagnant.createCopy();

        return new GenerationMetrics(organismsTopologyCopied, speciesTopologyCopied, organismsFitnessCopied, speciesAllFitnessCopied, speciesSharedFitnessCopied, speciesAgeCopied, speciesStagnationPeriodCopied, speciesStagnantCopied);
    }

    public void clear() {
        organismsTopology.clear();
        speciesTopology.clear();
        organismsFitness.clear();
        speciesAllFitness.clear();
        speciesSharedFitness.clear();
        speciesAge.clear();
        speciesStagnationPeriod.clear();
        speciesStagnant.clear();
    }
}
