package com.dipasquale.ai.rl.neat.speciation.metric;

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
public final class GenerationMetricData implements Serializable {
    @Serial
    private static final long serialVersionUID = 2802393466929099781L;
    private final Map<String, TopologyMetricData> organismsTopology;
    private final TopologyMetricData speciesTopology;
    private final List<FitnessMetricData> organismsFitness;
    private final MetricDatum speciesFitness;
    private final MetricDatum speciesAge;
    private final MetricDatum speciesStagnationPeriod;
    private final MetricDatum speciesStagnating;

    public GenerationMetricData createCopy() {
        Map<String, TopologyMetricData> organismsTopologyCopied = organismsTopology.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().createCopy()));

        TopologyMetricData speciesTopologyCopied = speciesTopology.createCopy();

        List<FitnessMetricData> organismsFitnessCopied = organismsFitness.stream()
                .map(FitnessMetricData::createCopy)
                .collect(Collectors.toList());

        MetricDatum speciesFitnessCopied = speciesFitness.createCopy();
        MetricDatum speciesAgeCopied = speciesAge.createCopy();
        MetricDatum speciesStagnationPeriodCopied = speciesStagnationPeriod.createCopy();
        MetricDatum speciesStagnatingCopied = speciesStagnating.createCopy();

        return new GenerationMetricData(organismsTopologyCopied, speciesTopologyCopied, organismsFitnessCopied, speciesFitnessCopied, speciesAgeCopied, speciesStagnationPeriodCopied, speciesStagnatingCopied);
    }

    public void clear() {
        organismsTopology.clear();
        speciesTopology.clear();
        organismsFitness.clear();
        speciesFitness.clear();
        speciesAge.clear();
        speciesStagnationPeriod.clear();
        speciesStagnating.clear();
    }
}
