package com.dipasquale.ai.rl.neat.speciation.metric;

import com.dipasquale.metric.MetricDatum;
import com.dipasquale.metric.MetricDatumFactory;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public final class FitnessMetrics implements Serializable {
    @Serial
    private static final long serialVersionUID = -1176164112821983540L;
    private final Map<String, MetricDatum> organisms;
    private final MetricDatum species;

    public FitnessMetrics(final MetricDatum species) {
        this(new HashMap<>(), species);
    }

    public static FitnessMetrics create(final MetricDatumFactory metricDatumFactory) {
        return new FitnessMetrics(metricDatumFactory.create());
    }

    public FitnessMetrics createCopy() {
        Map<String, MetricDatum> copiedOrganisms = organisms.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().createCopy()));

        MetricDatum copiedSpecies = species.createCopy();

        return new FitnessMetrics(new HashMap<>(copiedOrganisms), copiedSpecies);
    }

    public void clear() {
        organisms.clear();
        species.clear();
    }
}
