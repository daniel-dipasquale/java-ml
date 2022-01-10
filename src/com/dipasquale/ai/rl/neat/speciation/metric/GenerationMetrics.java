package com.dipasquale.ai.rl.neat.speciation.metric;

import com.dipasquale.common.factory.data.structure.map.MapFactory;
import com.dipasquale.metric.MetricDatum;
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
    private final Map<String, TopologyMetrics> organismsTopology;
    private final List<FitnessMetrics> fitnessCalculations;
    private final MetricDatum speciesAge;
    private final MetricDatum speciesStagnationPeriod;
    private final MetricDatum speciesStagnant;
    private final Map<String, MetricDatum> organismsKilled;
    private final MetricDatum speciesExtinct;

    public GenerationMetrics createCopy(final MapFactory mapFactory) {
        Map<String, TopologyMetrics> organismsTopologyCopied = organismsTopology.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().createCopy()));

        List<FitnessMetrics> fitnessCalculationsCopied = fitnessCalculations.stream()
                .map(fitnessMetrics -> fitnessMetrics.createCopy(mapFactory))
                .collect(Collectors.toList());

        MetricDatum speciesAgeCopied = speciesAge.createCopy();
        MetricDatum speciesStagnationPeriodCopied = speciesStagnationPeriod.createCopy();
        MetricDatum speciesStagnantCopied = speciesStagnant.createCopy();

        Map<String, MetricDatum> organismsKilledCopied = organismsKilled.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().createCopy()));

        MetricDatum speciesExtinctCopied = speciesExtinct.createCopy();

        return new GenerationMetrics(organismsTopologyCopied, fitnessCalculationsCopied, speciesAgeCopied, speciesStagnationPeriodCopied, speciesStagnantCopied, organismsKilledCopied, speciesExtinctCopied);
    }

    public void clear() {
        organismsTopology.clear();
        fitnessCalculations.clear();
        speciesAge.clear();
        speciesStagnationPeriod.clear();
        speciesStagnant.clear();
        speciesExtinct.clear();
    }
}
