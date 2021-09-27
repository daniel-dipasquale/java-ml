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
public final class IterationMetricData implements Serializable {
    @Serial
    private static final long serialVersionUID = 1590234190652192689L;
    private final Map<Integer, GenerationMetricData> generations;
    private final MetricDatum species;

    public IterationMetricData createCopy() {
        Map<Integer, GenerationMetricData> generationsCopied = generations.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().createCopy()));

        MetricDatum speciesCopied = species.createCopy();

        return new IterationMetricData(generationsCopied, speciesCopied);
    }

    public void clear() {
        generations.clear();
        species.clear();
    }
}
