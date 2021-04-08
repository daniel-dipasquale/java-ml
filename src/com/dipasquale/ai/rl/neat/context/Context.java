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
import com.dipasquale.threading.wait.handle.WaitHandle;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

public interface Context extends Serializable {
    GeneralSupport general();

    NodeGeneSupport nodes();

    ConnectionGeneSupport connections();

    NeuralNetworkSupport neuralNetwork();

    Parallelism parallelism();

    Random random();

    Mutation mutation();

    CrossOver crossOver();

    Speciation speciation();

    interface GeneralSupport extends Serializable {
        int populationSize();

        String createGenomeId();

        GenomeDefault createGenesisGenome(Context context);

        String createSpeciesId();

        FitnessDeterminer createFitnessDeterminer();

        float calculateFitness(GenomeDefault genome);

        void markToKill(GenomeDefault genome);

        int getGenomesKilledCount();

        void reset();
    }

    interface NodeGeneSupport extends Serializable {
        NodeGene create(NodeGeneType type);

        List<NodeGene> inputNodes();

        List<NodeGene> outputNodes();

        List<NodeGene> biasNodes();

        void reset();
    }

    interface ConnectionGeneSupport extends Serializable {
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

    @FunctionalInterface
    interface NeuralNetworkSupport extends Serializable {
        NeuralNetwork create(GenomeDefault genome, NodeGeneMap nodes, ConnectionGeneMap connections);
    }

    interface Parallelism extends Serializable {
        boolean isEnabled();

        int numberOfThreads();

        <T> WaitHandle forEach(Iterator<T> iterator, Consumer<T> action);
    }

    interface Random extends Serializable {
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

    interface Mutation extends Serializable {
        boolean shouldAddNodeMutation();

        boolean shouldAddConnectionMutation();

        boolean shouldPerturbConnectionWeight();

        boolean shouldReplaceConnectionWeight();

        boolean shouldDisableConnectionExpressed();
    }

    interface CrossOver extends Serializable {
        boolean shouldMateAndMutate();

        boolean shouldMateOnly();

        boolean shouldMutateOnly();

        boolean shouldOverrideConnectionExpressed();

        boolean shouldUseRandomParentConnectionWeight();

        default GenomeDefault crossOverBySkippingUnfitDisjointOrExcess(final Context context, final GenomeDefault fitParent, final GenomeDefault unfitParent) {
            return GenomeDefault.crossOverBySkippingUnfitDisjointOrExcess(context, fitParent, unfitParent);
        }

        default GenomeDefault crossOverByEqualTreatment(final Context context, final GenomeDefault parent1, final GenomeDefault parent2) {
            return GenomeDefault.crossOverByEqualTreatment(context, parent1, parent2);
        }
    }

    interface Speciation extends Serializable {
        int maximumSpecies();

        int maximumGenomes();

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
