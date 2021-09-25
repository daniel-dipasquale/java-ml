package com.dipasquale.ai.rl.neat.speciation.metric;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Getter
public final class GenerationMetricData implements Serializable {
    @Serial
    private static final long serialVersionUID = 2802393466929099781L;
    private final Map<String, TopologyMetricData> speciesTopology;
    private final TopologyMetricData topology;
    private final List<FitnessMetricData> fitness;

    public void clear() {
        speciesTopology.clear();
        topology.clear();
        fitness.clear();
    }
}
