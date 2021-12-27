package com.dipasquale.ai.rl.neat.core;

import com.dipasquale.common.time.DateTimeSupport;
import com.dipasquale.metric.LazyValuesMetricDatumFactory;
import com.dipasquale.metric.MetricDatum;
import com.dipasquale.metric.MetricDatumFactory;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public final class MetricCollectorTrainingPolicy implements NeatTrainingPolicy, Serializable { // TODO: come up with a plan for this
    @Serial
    private static final long serialVersionUID = 6023334719020977847L;
    private static final MetricDatumFactory METRIC_DATUM_FACTORY = new LazyValuesMetricDatumFactory();
    private final DateTimeSupport dateTimeSupport;
    private int lastGenerationTested = 0;
    private long lastGenerationDateTime = Long.MIN_VALUE;
    private final MetricDatum generationTimeMetricDatum = METRIC_DATUM_FACTORY.create();
    private final List<MetricDatum> generationTimeMetricData = new ArrayList<>();
    private int lastIterationTested = 0;
    private long lastIterationDateTime = Long.MIN_VALUE;
    private final MetricDatum iterationTimeMetricDatum = METRIC_DATUM_FACTORY.create();
    private float lastMaximumFitness = 0f;

    private static String format(final float value) {
        if (Float.compare(value, (float) Math.floor(value)) != 0) {
            return Float.toString(value);
        }

        return Integer.toString((int) value);
    }

    private static void echo(final String name, final int number, final MetricDatum timeMetricDatum, final float maximumFitness) {
        float lastTime = timeMetricDatum.getValues().get(timeMetricDatum.getValues().size() - 1);
        float averageTime = timeMetricDatum.getAverage();

        System.out.printf("%s: %d { time: (%s), average time: (%s), maximum fitness: (%s) }%n", name, number, format(lastTime), format(averageTime), format(maximumFitness));
    }

    @Override
    public NeatTrainingResult test(final NeatActivator activator) {
        int iteration = activator.getState().getIteration();
        int generation = activator.getState().getGeneration();

        if (lastGenerationTested < generation || lastIterationTested < iteration) {
            long dateTime = dateTimeSupport.now();
            float maximumFitness = activator.getState().getMaximumFitness();
            float maximumFitnessFixed = generation > 1 ? maximumFitness : lastMaximumFitness;

            if (generation > 1 || iteration > 1 && lastIterationTested < iteration) {
                int generationFixed = generation > 1 ? generation - 1 : lastGenerationTested;
                int iterationFixed = generation > 1 ? iteration : iteration - 1;

                generationTimeMetricDatum.add(dateTime - lastGenerationDateTime);
                System.out.printf("iteration: %d, ", iterationFixed);
                echo("generation", generationFixed, generationTimeMetricDatum, maximumFitnessFixed);
            }

            lastGenerationDateTime = dateTime;
            lastGenerationTested = generation;

            if (lastIterationTested < iteration) {
                if (iteration > 1) {
                    generationTimeMetricData.add(generationTimeMetricDatum.createCopy());
                    generationTimeMetricDatum.clear();
                    iterationTimeMetricDatum.add(dateTime - lastIterationDateTime);
                    echo("iteration", iteration - 1, iterationTimeMetricDatum, maximumFitnessFixed);
                    System.out.printf("=========================================%n");
                }

                lastIterationDateTime = dateTime;
                lastIterationTested = iteration;
            }

            lastMaximumFitness = maximumFitness;
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
    }

    @Override
    public NeatTrainingPolicy createClone() {
        return new MetricCollectorTrainingPolicy(dateTimeSupport);
    }
}
