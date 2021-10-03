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
    private final MetricDatum all;
    private final MetricDatum shared;

    public FitnessMetrics createCopy(final MapFactory mapFactory) {
        Map<String, MetricDatum> organismsCopied = organisms.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().createCopy()));

        MetricDatum allCopied = all.createCopy();
        MetricDatum sharedCopied = shared.createCopy();

        return new FitnessMetrics(mapFactory.create(organismsCopied), allCopied, sharedCopied);
    }

    public void clear() {
        organisms.clear();
        all.clear();
        shared.clear();
    }
}
