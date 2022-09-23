package com.dipasquale.ai.rl.neat.speciation.metric;

import com.dipasquale.data.structure.map.UnionMap;
import com.dipasquale.metric.MetricDatum;
import com.dipasquale.metric.MetricDatumFactory;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class AbstractMetricsContainer implements MetricsContainer {
    private final MetricDatumFactory metricDatumFactory;
    private final FitnessMetrics fitnessMetrics;
    private final GenerationMetrics generationMetrics;
    private final IterationMetrics iterationMetrics;

    protected AbstractMetricsContainer(final MetricDatumFactory metricDatumFactory, final IterationMetrics iterationMetrics) {
        this(metricDatumFactory, FitnessMetrics.create(metricDatumFactory), GenerationMetrics.create(metricDatumFactory), iterationMetrics);
    }

    @Override
    public void addSpeciesComposition(final int age, final int stagnationPeriod, final boolean isStagnant) {
        generationMetrics.getSpeciesAge().add(age);
        generationMetrics.getSpeciesStagnationPeriod().add(stagnationPeriod);
        generationMetrics.getSpeciesStagnant().add(isStagnant);
    }

    private TopologyMetrics provideTopologyMetrics(final TopologyMetrics oldTopologyMetrics, final int hiddenNodeGenes, final int connectionGenes) {
        TopologyMetrics topologyMetrics = oldTopologyMetrics;

        if (topologyMetrics == null) {
            topologyMetrics = TopologyMetrics.create(metricDatumFactory);
        }

        topologyMetrics.getHiddenNodeGenes().add(hiddenNodeGenes);
        topologyMetrics.getConnectionGenes().add(connectionGenes);

        return topologyMetrics;
    }

    @Override
    public void addOrganismTopology(final String speciesId, final int hiddenNodeGenes, final int connectionGenes) {
        generationMetrics.getOrganismsTopology().compute(speciesId, (__, oldTopologyMetrics) -> provideTopologyMetrics(oldTopologyMetrics, hiddenNodeGenes, connectionGenes));
    }

    private MetricDatum provideOrganisms(final MetricDatum oldOrganisms, final float fitness) {
        MetricDatum organisms = oldOrganisms;

        if (organisms == null) {
            organisms = metricDatumFactory.create();
        }

        organisms.add(fitness);

        return organisms;
    }

    @Override
    public void addOrganismFitness(final String speciesId, final float fitness) {
        fitnessMetrics.getOrganisms().compute(speciesId, (__, oldOrganisms) -> provideOrganisms(oldOrganisms, fitness));
    }

    @Override
    public void addSpeciesFitness(final float fitness) {
        fitnessMetrics.getSpecies().add(fitness);
    }

    private MetricDatum provideOrganismsKilled(final MetricDatum oldOrganismsKilled, final int count) {
        MetricDatum organismsKilled = oldOrganismsKilled;

        if (organismsKilled == null) {
            organismsKilled = metricDatumFactory.create();
        }

        organismsKilled.add(count);

        return organismsKilled;
    }

    @Override
    public void addOrganismsKilled(final String speciesId, final int count) {
        generationMetrics.getOrganismsKilled().compute(speciesId, (__, oldOrganismsKilled) -> provideOrganismsKilled(oldOrganismsKilled, count));
    }

    @Override
    public void addSpeciesExtinction(final boolean extinct) {
        generationMetrics.getSpeciesExtinct().add(extinct);
    }

    @Override
    public void clearFitnessEvaluations() {
        generationMetrics.getFitnessEvaluations().clear();
    }

    protected void addFitness(final FitnessMetrics fitnessMetrics) {
        generationMetrics.getFitnessEvaluations().add(fitnessMetrics.createCopy());
    }

    @Override
    public void commitFitness() {
        addFitness(fitnessMetrics.createCopy());
        fitnessMetrics.clear();
    }

    @Override
    public void clearPreviousGenerations() {
        iterationMetrics.getGenerations().clear();
    }

    @Override
    public void commitGeneration(final int generation) {
        iterationMetrics.getGenerations().put(generation, generationMetrics.createCopy());
        generationMetrics.clear();
    }

    @Override
    public IterationMetrics commitIteration() {
        try {
            return iterationMetrics.createCopy();
        } finally {
            iterationMetrics.clear();
        }
    }

    @Override
    public IterationMetrics createInterimIterationCopy(final int currentGeneration) {
        Map<Integer, GenerationMetrics> currentGenerations = Map.of(currentGeneration, generationMetrics.createCopy());
        Map<Integer, GenerationMetrics> generations = new UnionMap<>(currentGenerations, iterationMetrics.getGenerations());

        return new IterationMetrics(generations);
    }

    @Override
    public IterationMetrics createIterationCopy() {
        return iterationMetrics.createCopy();
    }
}
