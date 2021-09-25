package com.dipasquale.ai.rl.neat.synchronization.dual.mode.metric;

import com.dipasquale.ai.rl.neat.speciation.metric.MetricData;
import com.dipasquale.synchronization.dual.mode.data.structure.map.DualModeMap;
import lombok.AllArgsConstructor;

import java.io.Serial;
import java.util.Map;

@AllArgsConstructor
public final class DualModeNoopMetricDataCollector extends DualModeMetricDataCollector {
    @Serial
    private static final long serialVersionUID = 5072714745845486203L;
    private boolean parallel;

    @Override
    public void addTopology(final String speciesId, final int hiddenNodes, final int connections) {
    }

    @Override
    public void addFitness(final String speciesId, final float fitness) {
    }

    @Override
    public void prepareNextFitnessCalculation() {
    }

    @Override
    public void prepareNextGeneration(final int currentGeneration) {
    }

    @Override
    public void prepareNextIteration(final Map<Integer, MetricData> allMetrics, final int iteration) {
    }

    @Override
    public void switchMode(final boolean concurrent) {
        parallel = concurrent;
    }

    @Override
    public DualModeMap<Integer, MetricData> ensureMode(final DualModeMap<Integer, MetricData> allMetrics, final int numberOfThreads) {
        return new DualModeMap<>(parallel, numberOfThreads, allMetrics);
    }
}
