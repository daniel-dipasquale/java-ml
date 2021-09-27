package com.dipasquale.ai.rl.neat.speciation.metric;

import com.dipasquale.metric.EmptyValuesMetricDatum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Getter
public final class NoopMetricDataCollector implements MetricDataCollector, Serializable {
    @Serial
    private static final long serialVersionUID = -3442321706503755754L;
    private final FitnessMetricData currentFitness = new FitnessMetricData(new HashMap<>(), new EmptyValuesMetricDatum());
    private final GenerationMetricData currentGeneration = createEmptyGeneration();
    private final IterationMetricData currentIteration = new IterationMetricData(new HashMap<>(), new EmptyValuesMetricDatum());

    private static GenerationMetricData createEmptyGeneration() {
        TopologyMetricData emptyTopology = new TopologyMetricData(new EmptyValuesMetricDatum(), new EmptyValuesMetricDatum());

        return new GenerationMetricData(new HashMap<>(), emptyTopology, new ArrayList<>(), new EmptyValuesMetricDatum(), new EmptyValuesMetricDatum(), new EmptyValuesMetricDatum(), new EmptyValuesMetricDatum());
    }

    @Override
    public void addOrganismTopology(final String speciesId, final int hiddenNodes, final int connections) {
    }

    @Override
    public void addOrganismFitness(final String speciesId, final float fitness) {
    }

    @Override
    public void addSpeciesAttributes(final int age, final int stagnationPeriod, final boolean isStagnant) {
    }

    @Override
    public void addSpeciesFitness(final float fitness) {
    }

    @Override
    public void prepareNextFitnessCalculation() {
    }

    @Override
    public void prepareNextGeneration(final int currentGeneration) {
    }

    @Override
    public void prepareNextIteration(final Map<Integer, IterationMetricData> iterationsMetrics, final int currentIteration) {
    }
}
