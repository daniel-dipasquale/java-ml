package com.dipasquale.ai.rl.neat;

import com.dipasquale.ai.rl.neat.genotype.Genome;
import com.dipasquale.ai.rl.neat.genotype.NodeGeneType;
import com.dipasquale.common.time.DateTimeSupport;
import com.dipasquale.metric.MetricDatum;
import com.dipasquale.metric.MetricDatumFactory;
import com.dipasquale.metric.StandardMetricDatumFactory;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public final class MetricCollectorTrainingPolicy implements NeatTrainingPolicy, Serializable { // TODO: come up with a plan for this
    @Serial
    private static final long serialVersionUID = 6023334719020977847L;
    private static final MetricDatumFactory METRIC_DATUM_FACTORY = StandardMetricDatumFactory.getInstance();
    private final DateTimeSupport dateTimeSupport;
    private int lastGenerationTested = 0;
    private long lastGenerationDateTime = Long.MIN_VALUE;
    private final MetricDatum generationTimeMetricDatum = METRIC_DATUM_FACTORY.create();
    private final List<MetricDatum> generationTimeMetricData = new ArrayList<>();
    private int lastIterationTested = 0;
    private long lastIterationDateTime = Long.MIN_VALUE;
    private final MetricDatum iterationTimeMetricDatum = METRIC_DATUM_FACTORY.create();
    private float lastMaximumFitness = 0f;
    private int lastSpeciesCount = 0;
    private Genome lastChampionGenome = null;

    private static String format(final float value) {
        if (Double.compare(value, Math.floor(value)) != 0) {
            return Float.toString(value);
        }

        return Integer.toString((int) value);
    }

    private static void echo(final String name, final int number, final MetricDatum timeMetricDatum, final float maximumFitness, final int species, final Genome genome) {
        String messageFormat = "%s: %d { time: (%s), average time: (%s), species: (%d), hidden nodes: (%d), expressed connections: (%d), total connections: (%d), maximum fitness: (%s) }%n";
        float time = timeMetricDatum.getLastValue();
        float averageTime = timeMetricDatum.getAverage();
        int hiddenNodes = genome.getNodeGenes().size(NodeGeneType.HIDDEN);
        int expressedConnections = genome.getConnectionGenes().getExpressed().size();
        int totalConnections = genome.getConnectionGenes().getAll().size();

        System.out.printf(messageFormat, name, number, format(time), format(averageTime), species, hiddenNodes, expressedConnections, totalConnections, format(maximumFitness));
    }

    @Override
    public NeatTrainingResult test(final NeatActivator activator) {
        NeatState state = activator.getState();
        int iteration = state.getIteration();
        int generation = state.getGeneration();

        if (lastGenerationTested < generation || lastIterationTested < iteration) {
            long dateTime = dateTimeSupport.now();
            float maximumFitness = state.getMaximumFitness();

            float fixedMaximumFitness = generation > 1
                    ? maximumFitness
                    : lastMaximumFitness;

            int speciesCount = state.getSpeciesCount();

            int fixedSpeciesCount = generation > 1
                    ? speciesCount
                    : lastSpeciesCount;

            Genome championGenome = state.getChampionGenome();

            Genome fixedChampionGenome = generation > 1
                    ? championGenome
                    : lastChampionGenome;

            if (generation > 1 || iteration > 1 && lastIterationTested < iteration) {
                int fixedGeneration = generation > 1
                        ? generation - 1
                        : lastGenerationTested;

                int fixedIteration = generation > 1
                        ? iteration
                        : iteration - 1;

                generationTimeMetricDatum.add(dateTime - lastGenerationDateTime);
                System.out.printf("iteration: %d, ", fixedIteration);
                echo("generation", fixedGeneration, generationTimeMetricDatum, fixedMaximumFitness, fixedSpeciesCount, fixedChampionGenome);
            }

            lastGenerationDateTime = dateTime;
            lastGenerationTested = generation;

            if (lastIterationTested < iteration) {
                if (iteration > 1) {
                    generationTimeMetricData.add(generationTimeMetricDatum.createCopy());
                    generationTimeMetricDatum.clear();
                    iterationTimeMetricDatum.add(dateTime - lastIterationDateTime);
                    echo("iteration", iteration - 1, iterationTimeMetricDatum, fixedMaximumFitness, fixedSpeciesCount, fixedChampionGenome);
                    System.out.printf("=========================================%n");
                }

                lastIterationDateTime = dateTime;
                lastIterationTested = iteration;
            }

            lastMaximumFitness = maximumFitness;
            lastSpeciesCount = speciesCount;
            lastChampionGenome = championGenome;
        }

        return NeatTrainingResult.CONTINUE_TRAINING;
    }

    @Override
    public void reset() {
        lastGenerationTested = 0;
        lastGenerationDateTime = Long.MIN_VALUE;
        generationTimeMetricDatum.clear();
        generationTimeMetricData.clear();
        lastIterationTested = 0;
        lastIterationDateTime = Long.MIN_VALUE;
        iterationTimeMetricDatum.clear();
        lastMaximumFitness = 0f;
        lastSpeciesCount = 0;
        lastChampionGenome = null;
    }

    @Override
    public NeatTrainingPolicy createClone() {
        return new MetricCollectorTrainingPolicy(dateTimeSupport);
    }
}
