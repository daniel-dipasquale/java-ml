package com.dipasquale.ai.rl.neat.speciation.metric;

import com.dipasquale.metric.MetricDatum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.Map;

@RequiredArgsConstructor
@Getter
public final class IterationMetrics implements Serializable {
    @Serial
    private static final long serialVersionUID = 1590234190652192689L;
    private final Map<Integer, GenerationMetrics> generations;
    private final MetricDatum speciesCount;

    public void clear() {
        generations.clear();
        speciesCount.clear();
    }
}
