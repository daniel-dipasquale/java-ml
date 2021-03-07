package com.dipasquale.ai.rl.neat;

import com.dipasquale.ai.common.SequentialId;

import java.util.List;

interface Context {
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
        NeuralNetwork create(GenomeDefault genome);
    }

    @FunctionalInterface
    interface NodeGeneSupport {
        NodeGene create(NodeGeneType type);
    }

    interface ConnectionGeneSupport {
        boolean allowRecurrentConnections(); // TODO: consider this to be a probability rate

        InnovationId getOrCreateInnovationId(DirectedEdge directedEdge);

        default InnovationId getOrCreateInnovationId(final SequentialId inNodeId, final SequentialId outNodeId) {
            DirectedEdge directedEdge = new DirectedEdge(inNodeId, outNodeId);

            return getOrCreateInnovationId(directedEdge);
        }

        default InnovationId getOrCreateInnovationId(final NodeGene inNode, final NodeGene outNode) {
            return getOrCreateInnovationId(inNode.getId(), outNode.getId());
        }

        float nextWeight(); // next() * 4 - 2
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

            return items.get(nextIndex(offset, size));
        }

        default <T> T nextItem(final List<T> items) {
            return nextItem(items, 0);
        }

        default <T> T nextItem(final SequentialMap<? extends Comparable<?>, T> items) {
            int size = items.size();

            if (size == 0) {
                return null;
            }

            return items.getByIndex(nextIndex(size));
        }

        float next();

        float next(float min, float max);

        boolean isLessThan(float rate);
    }

    interface Mutation {
        float addNodeMutationsRate(); // 0.1

        float addConnectionMutationsRate(); // 0.1

        float perturbConnectionWeightRate(); // 0.9

        float changeConnectionExpressedRate(); // 0.2
    }

    interface CrossOver {
        float rate(); // 0.8

        float enforceExpressedRate(); // 0.5

        float useRandomParentWeightRate(); // 1.0

        GenomeDefault crossOverBySkippingUnfitDisjointOrExcess(GenomeDefault fitParent, GenomeDefault unfitParent);

        GenomeDefault crossOverByEqualTreatment(GenomeDefault parent1, GenomeDefault parent2);
    }

    interface Speciation {
        int maximumGenomes();

        float weightDifferenceCoefficient(); // 1.0

        float disjointCoefficient(); // 2.0

        float excessCoefficient(); // 2.0

        float compatibilityThreshold(int generation); // 6.0 ( * compatibilityThresholdModifier ^ generation )

        float calculateCompatibility(GenomeDefault genome1, GenomeDefault genome2);

        default boolean belongs(final GenomeDefault genome1, final GenomeDefault genome2, final int generation) {
            float compatibility = calculateCompatibility(genome1, genome2);

            return Float.compare(compatibility, compatibilityThreshold(generation)) < 0;
        }

        float eugenicsThreshold(); // 0.2

        float elitistThreshold(); // 0.01

        int elitistThresholdMinimum(); // 1

        int stagnationDropOffAge(); // 15

        float interspeciesMatingRate(); // 0.01
    }
}
