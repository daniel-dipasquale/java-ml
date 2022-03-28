package com.dipasquale.ai.rl.neat.speciation.metric;

import com.dipasquale.common.factory.data.structure.map.MapFactory;
import com.dipasquale.metric.MetricDatum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Getter
public final class FitnessMetrics implements Serializable {
    @Serial
    private static final long serialVersionUID = -1176164112821983540L;
    private final Map<String, MetricDatum> organisms;
    private final MetricDatum shared;

    public FitnessMetrics createCopy(final MapFactory mapFactory) {
        Map<String, MetricDatum> copiedOrganisms = organisms.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().createCopy()));

        MetricDatum copiedShared = shared.createCopy();

        return new FitnessMetrics(mapFactory.create(copiedOrganisms), copiedShared);
    }

    public void clear() {
        organisms.clear();
        shared.clear();
    }
}
