package com.dipasquale.ai.rl.neat.speciation.metric;

import com.dipasquale.metric.MetricDatum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.Map;

@RequiredArgsConstructor
@Getter
public final class FitnessMetricData implements Serializable {
    @Serial
    private static final long serialVersionUID = -1176164112821983540L;
    private final Map<String, MetricDatum> species;
    private final MetricDatum all;

    public void clear() {
        species.clear();
        all.clear();
    }
}
