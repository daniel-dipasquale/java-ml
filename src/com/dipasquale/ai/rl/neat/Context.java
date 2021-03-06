package com.dipasquale.ai.rl.neat;

import java.util.List;

interface Context<T extends Comparable<T>> {
    GeneralSupport<T> general();

    NodeGeneSupport<T> nodes();

    ConnectionGeneSupport<T> connections();

    NeuralNetworkSupport<T> neuralNetwork();

    Random random();

    Mutation mutation();

    CrossOver<T> crossOver();

    Speciation<T> speciation();

    interface GeneralSupport<T extends Comparable<T>> {
        int populationSize(); // 150

        String createGenomeId();

        GenomeDefault<T> createGenesisGenome();

        String createSpeciesId();

        FitnessDeterminer createFitnessDeterminer();

        float calculateFitness(GenomeDefault<T> genome);
    }

    @FunctionalInterface
    interface NeuralNetworkSupport<T extends Comparable<T>> {
        NeuralNetwork create(GenomeDefault<T> genome);
    }

    @FunctionalInterface
    interface NodeGeneSupport<T extends Comparable<T>> {
        NodeGene<T> create(NodeGeneType type);
    }

    interface ConnectionGeneSupport<T extends Comparable<T>> {
        boolean allowRecurrentConnections(); // TODO: consider this to be a probability rate

        InnovationId<T> getOrCreateInnovationId(DirectedEdge<T> directedEdge);

        default InnovationId<T> getOrCreateInnovationId(final T inNodeId, final T outNodeId) {
            DirectedEdge<T> directedEdge = new DirectedEdge<>(inNodeId, outNodeId);

            return getOrCreateInnovationId(directedEdge);
        }

        default InnovationId<T> getOrCreateInnovationId(final NodeGene<T> inNode, final NodeGene<T> outNode) {
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

    interface CrossOver<T extends Comparable<T>> {
        float rate(); // 0.8

        float enforceExpressedRate(); // 0.5

        float useRandomParentWeightRate(); // 1.0

        GenomeDefault<T> crossOverBySkippingUnfitDisjointOrExcess(GenomeDefault<T> fitParent, GenomeDefault<T> unfitParent);

        GenomeDefault<T> crossOverByEqualTreatment(GenomeDefault<T> parent1, GenomeDefault<T> parent2);
    }

    interface Speciation<T extends Comparable<T>> {
        int maximumGenomes();

        float weightDifferenceCoefficient(); // 1.0

        float disjointCoefficient(); // 2.0

        float excessCoefficient(); // 2.0

        float compatibilityThreshold(int generation); // 6.0 ( * compatibilityThresholdModifier ^ generation )

        float calculateCompatibility(GenomeDefault<T> genome1, GenomeDefault<T> genome2);

        default boolean belongs(final GenomeDefault<T> genome1, final GenomeDefault<T> genome2, final int generation) {
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
