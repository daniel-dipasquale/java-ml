package com.dipasquale.ai.rl.neat.context;

import com.dipasquale.ai.common.FitnessDeterminer;
import com.dipasquale.ai.common.SequentialId;
import com.dipasquale.ai.common.SequentialMap;
import com.dipasquale.ai.rl.neat.genotype.ConnectionGeneMap;
import com.dipasquale.ai.rl.neat.genotype.DirectedEdge;
import com.dipasquale.ai.rl.neat.genotype.GenomeDefault;
import com.dipasquale.ai.rl.neat.genotype.InnovationId;
import com.dipasquale.ai.rl.neat.genotype.NodeGene;
import com.dipasquale.ai.rl.neat.genotype.NodeGeneMap;
import com.dipasquale.ai.rl.neat.genotype.NodeGeneType;
import com.dipasquale.ai.rl.neat.phenotype.NeuralNetwork;
import com.dipasquale.common.Pair;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

public interface Context {
    GeneralSupport general();

    NodeGeneSupport nodes();

    ConnectionGeneSupport connections();

    NeuralNetworkSupport neuralNetwork();

    Parallelism parallelism();

    Random random();

    Mutation mutation();

    CrossOver crossOver();

    Speciation speciation();

    interface GeneralSupport {
        int populationSize();

        String createGenomeId();

        GenomeDefault createGenesisGenome();

        String createSpeciesId();

        FitnessDeterminer createFitnessDeterminer();

        float calculateFitness(GenomeDefault genome);

        void discardGenome(GenomeDefault genome);

        void reset();
    }

    @FunctionalInterface
    interface NeuralNetworkSupport {
        NeuralNetwork create(GenomeDefault genome, NodeGeneMap nodes, ConnectionGeneMap connections);
    }

    interface NodeGeneSupport {
        NodeGene create(NodeGeneType type);

        void reset();
    }

    interface ConnectionGeneSupport {
        boolean multipleRecurrentCyclesAllowed();

        InnovationId getOrCreateInnovationId(DirectedEdge directedEdge);

        default InnovationId getOrCreateInnovationId(final SequentialId inNodeId, final SequentialId outNodeId) {
            DirectedEdge directedEdge = new DirectedEdge(inNodeId, outNodeId);

            return getOrCreateInnovationId(directedEdge);
        }

        default InnovationId getOrCreateInnovationId(final NodeGene inNode, final NodeGene outNode) {
            return getOrCreateInnovationId(inNode.getId(), outNode.getId());
        }

        float nextWeight();

        float perturbWeight(float weight);

        void reset();
    }

    interface Random {
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

    interface Parallelism {
        boolean isEnabled();

        int numberOfThreads();

        <T> void forEach(Stream<T> stream, Consumer<T> action);

        void waitUntilDone() throws InterruptedException;

        void shutdown();
    }

    interface Mutation {
        boolean shouldAddNodeMutation();

        boolean shouldAddConnectionMutation();

        boolean shouldPerturbConnectionWeight();

        boolean shouldReplaceConnectionWeight();

        boolean shouldDisableConnectionExpressed();
    }

    interface CrossOver {
        boolean shouldMateAndMutate();

        boolean shouldMateOnly();

        boolean shouldMutateOnly();

        boolean shouldOverrideConnectionExpressed();

        boolean shouldUseRandomParentConnectionWeight();

        GenomeDefault crossOverBySkippingUnfitDisjointOrExcess(GenomeDefault fitParent, GenomeDefault unfitParent);

        GenomeDefault crossOverByEqualTreatment(GenomeDefault parent1, GenomeDefault parent2);
    }

    interface Speciation {
        int maximumSpecies();

        int maximumGenomes();

        float weightDifferenceCoefficient(); // c3

        float disjointCoefficient(); // c2

        float excessCoefficient(); // c1

        double compatibilityThreshold(int generation);

        double calculateCompatibility(GenomeDefault genome1, GenomeDefault genome2);

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
}
