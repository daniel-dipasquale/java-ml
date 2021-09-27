package com.dipasquale.ai.rl.neat.speciation.metric;

import com.dipasquale.metric.MetricDatum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Getter
public final class FitnessMetricData implements Serializable {
    @Serial
    private static final long serialVersionUID = -1176164112821983540L;
    private final Map<String, MetricDatum> organisms;
    private final MetricDatum species;

    public FitnessMetricData createCopy() {
        Map<String, MetricDatum> organismsCopied = organisms.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().createCopy()));

        MetricDatum speciesCopied = species.createCopy();

        return new FitnessMetricData(organismsCopied, speciesCopied);
    }

    public void clear() {
        organisms.clear();
        species.clear();
    }
}
