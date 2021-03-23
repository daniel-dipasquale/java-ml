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

import java.util.List;

public interface Context {
    GeneralSupport general();

    NodeGeneSupport nodes();

    ConnectionGeneSupport connections();

    NeuralNetworkSupport neuralNetwork();

    Random random();

    Mutation mutation();

    CrossOver crossOver();

    Speciation speciation();

    interface GeneralSupport {
        int populationSize(); // 150

        String createGenomeId();

        GenomeDefault createGenesisGenome();

        String createSpeciesId();

        FitnessDeterminer createFitnessDeterminer();

        float calculateFitness(GenomeDefault genome);
    }

    @FunctionalInterface
    interface NeuralNetworkSupport {
        NeuralNetwork create(GenomeDefault genome, NodeGeneMap nodes, ConnectionGeneMap connections);
    }

    @FunctionalInterface
    interface NodeGeneSupport {
        NodeGene create(NodeGeneType type);
    }

    interface ConnectionGeneSupport {
        boolean recurrentConnectionsAllowed();

        boolean multipleRecurrentCyclesAllowed();

        InnovationId getOrCreateInnovationId(DirectedEdge directedEdge);

        default InnovationId getOrCreateInnovationId(final SequentialId inNodeId, final SequentialId outNodeId) {
            DirectedEdge directedEdge = new DirectedEdge(inNodeId, outNodeId);

            return getOrCreateInnovationId(directedEdge);
        }

        default InnovationId getOrCreateInnovationId(final NodeGene inNode, final NodeGene outNode) {
            return getOrCreateInnovationId(inNode.getId(), outNode.getId());
        }

        float nextWeight(); // next() * 4 - 2

        float perturbWeight(float weight);
    }

    interface Random {
        int nextIndex(int offset, int count);

        default int nextIndex(final int count) {
            return nextIndex(0, count);
        }

        default <T> T nextItem(final List<T> items, final int offset) {
            int size = items.size();

            if (size == 0) {
                return null;
            }

            int index = nextIndex(offset, size);

            return items.get(index);
        }

        default <T> T nextItem(final List<T> items) {
            return nextItem(items, 0);
        }

        default <T> T nextItem(final SequentialMap<? extends Comparable<?>, T> items) {
            int size = items.size();

            if (size == 0) {
                return null;
            }

            int index = nextIndex(size);

            return items.getByIndex(index);
        }

        boolean isLessThan(float rate);
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

        float compatibilityThreshold(int generation); // (compatibilityThreshold) ( * compatibilityThresholdModifier ^ generation )

        float calculateCompatibility(GenomeDefault genome1, GenomeDefault genome2);

        default boolean belongs(final GenomeDefault genome1, final GenomeDefault genome2, final int generation) {
            float compatibility = calculateCompatibility(genome1, genome2);

            return Float.compare(compatibility, compatibilityThreshold(generation)) < 0;
        }

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
