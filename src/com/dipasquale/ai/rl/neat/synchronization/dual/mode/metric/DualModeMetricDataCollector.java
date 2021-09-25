package com.dipasquale.ai.rl.neat.synchronization.dual.mode.metric;

import com.dipasquale.ai.rl.neat.speciation.metric.MetricData;
import com.dipasquale.ai.rl.neat.speciation.metric.MetricDataCollector;
import com.dipasquale.synchronization.dual.mode.DualModeObject;
import com.dipasquale.synchronization.dual.mode.data.structure.map.DualModeMap;

import java.io.Serial;
import java.io.Serializable;

public abstract class DualModeMetricDataCollector implements DualModeObject, MetricDataCollector, Serializable {
    @Serial
    private static final long serialVersionUID = -30170807481135566L;

    public abstract DualModeMap<Integer, MetricData> ensureMode(DualModeMap<Integer, MetricData> allMetrics, int numberOfThreads);
}
