package com.dipasquale.ai.rl.neat.context;

import com.dipasquale.ai.common.fitness.FitnessDeterminer;
import com.dipasquale.ai.common.fitness.FitnessFunction;
import com.dipasquale.ai.common.sequence.SequentialId;
import com.dipasquale.ai.common.sequence.SequentialMap;
import com.dipasquale.ai.rl.neat.genotype.ConnectionGeneMap;
import com.dipasquale.ai.rl.neat.genotype.DefaultGenome;
import com.dipasquale.ai.rl.neat.genotype.Genome;
import com.dipasquale.ai.rl.neat.genotype.NodeGene;
import com.dipasquale.ai.rl.neat.genotype.NodeGeneMap;
import com.dipasquale.ai.rl.neat.genotype.NodeGeneType;
import com.dipasquale.ai.rl.neat.phenotype.NeuralNetwork;
import com.dipasquale.ai.rl.neat.speciation.core.PopulationHistoricalMarkings;
import com.dipasquale.common.Pair;
import com.dipasquale.threading.event.loop.IterableEventLoop;
import com.dipasquale.threading.wait.handle.WaitHandle;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

public interface Context {
    GeneralSupport general();

    NodeGeneSupport nodes();

    ConnectionGeneSupport connections();

    NeuralNetworkSupport neuralNetwork();

    ParallelismSupport parallelism();

    RandomSupport random();

    MutationSupport mutation();

    CrossOverSupport crossOver();

    SpeciationSupport speciation();

    void save(ObjectOutputStream outputStream) throws IOException;

    void load(ObjectInputStream inputStream, StateOverrideSupport override) throws IOException, ClassNotFoundException;

    interface GeneralSupport {
        int populationSize();

        FitnessDeterminer createFitnessDeterminer();

        float calculateFitness(DefaultGenome genome);
    }

    interface NodeGeneSupport {
        NodeGene create(SequentialId id, NodeGeneType type);

        int size(NodeGeneType type);
    }

    interface ConnectionGeneSupport {
        boolean multipleRecurrentCyclesAllowed();

        float nextWeight();

        float perturbWeight(float weight);

        void setupInitialConnections(DefaultGenome genome, PopulationHistoricalMarkings historicalMarkings);
    }

    @FunctionalInterface
    interface NeuralNetworkSupport {
        NeuralNetwork create(DefaultGenome genome, NodeGeneMap nodes, ConnectionGeneMap connections);
    }

    interface ParallelismSupport {
        boolean isEnabled();

        int numberOfThreads();

        <T> WaitHandle forEach(Iterator<T> iterator, Consumer<T> itemHandler);
    }

    interface RandomSupport {
        int nextIndex(int offset, int count);

        default int nextIndex(final int count) {
            return nextIndex(0, count);
        }

        default <T> T nextItem(final List<T> items) {
            int size = items.size();

            if (size == 0) {
                return null;
            }

            int index = nextIndex(size);

            return items.get(index);
        }

        boolean isLessThan(float rate);

        default <T> Pair<T> nextUniquePair(final List<T> items) {
            int size = items.size();

            if (size <= 1) {
                return null;
            }

            if (size == 2) {
                return new Pair<>(items.get(0), items.get(1));
            }

            int index1 = nextIndex(size);
            float first = (float) index1;
            float total = (float) (size - 1);

            int index2 = isLessThan(first / total)
                    ? nextIndex(index1)
                    : nextIndex(index1 + 1, size);

            return new Pair<>(items.get(index1), items.get(index2));
        }

        default <T> T nextItem(final SequentialMap<? extends Comparable<?>, T> items) {
            int size = items.size();

            if (size == 0) {
                return null;
            }

            int index = nextIndex(size);

            return items.getByIndex(index);
        }
    }

    interface MutationSupport {
        boolean shouldAddNodeMutation();

        boolean shouldAddConnectionMutation();

        boolean shouldPerturbConnectionWeight();

        boolean shouldReplaceConnectionWeight();

        boolean shouldDisableConnectionExpressed();
    }

    interface CrossOverSupport {
        boolean shouldMateAndMutate();

        boolean shouldMateOnly();

        boolean shouldMutateOnly();

        boolean shouldOverrideConnectionExpressed();

        boolean shouldUseRandomParentConnectionWeight();

        default DefaultGenome crossOverBySkippingUnfitDisjointOrExcess(final Context context, final DefaultGenome fitParent, final DefaultGenome unfitParent) {
            return DefaultGenome.crossOverBySkippingUnfitDisjointOrExcess(context, fitParent, unfitParent);
        }

        default DefaultGenome crossOverByEqualTreatment(final Context context, final DefaultGenome parent1, final DefaultGenome parent2) {
            return DefaultGenome.crossOverByEqualTreatment(context, parent1, parent2);
        }
    }

    interface SpeciationSupport {
        int maximumSpecies();

        int maximumGenomes();

        double compatibilityThreshold(int generation);

        double calculateCompatibility(DefaultGenome genome1, DefaultGenome genome2);

        float eugenicsThreshold();

        default int getFitCountToReproduce(final int size) {
            int count = (int) Math.floor((double) eugenicsThreshold() * (double) size);
            int countFixed = Math.max(count, 1);

            return Math.min(countFixed, size);
        }

        float elitistThreshold();

        int elitistThresholdMinimum();

        default int getEliteCountToPreserve(final int size) {
            int count = (int) Math.floor((double) elitistThreshold() * (double) size);
            int countFixed = Math.max(count, elitistThresholdMinimum());

            return Math.min(countFixed, size);
        }

        int stagnationDropOffAge();

        float interSpeciesMatingRate();
    }

    interface StateOverrideSupport {
        FitnessFunction<Genome> environment();

        IterableEventLoop eventLoop();
    }
}