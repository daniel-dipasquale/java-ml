package com.experimental.ai.rl.neat;

import java.util.List;

interface Context<T extends Comparable<T>> {
    GeneralSupport<T> general();

    NodeGeneSupport<T> nodes();

    ConnectionGeneSupport<T> connections();

    NeuralNetworkSupport neuralNetwork();

    Random random();

    Mutation mutation();

    CrossOver crossover();

    Speciation<T> speciation();

    interface GeneralSupport<T extends Comparable<T>> {
        int populationSize(); // 100

        int maximumGenerations();

        GenomeDefault<T> createGenesisGenome();

        float calculateFitness(GenomeDefault<T> genome);
    }

    @FunctionalInterface
    interface NeuralNetworkSupport {
        NeuralNetwork create(Genome genome);
    }

    @FunctionalInterface
    interface NodeGeneSupport<T extends Comparable<T>> {
        NodeGene<T> create(NodeGene.Type type);
    }

    interface ConnectionGeneSupport<T extends Comparable<T>> {
        boolean allowCyclicConnections();

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
        int nextIndex(int count);

        default <T> T nextItem(final List<T> items) {
            int size = items.size();

            if (size == 0) {
                return null;
            }

            return items.get(nextIndex(size));
        }

        default <T> T nextItem(final SequentialMap<? extends Comparable<?>, T> items) {
            int size = items.size();

            if (size == 0) {
                return null;
            }

            return items.getByIndex(nextIndex(size));
        }

        float next();

        boolean isAtMost(float rate);
    }

    interface Mutation {
        float addNodeMutationsRate(); // 0.1

        float addConnectionMutationsRate(); // 0.1

        float perturbConnectionWeightRate(); // 0.9

        float changeConnectionExpressedRate(); // 0.2
    }

    interface CrossOver {
        float rate(); // 0.8

        float disableExpressedInheritanceRate(); // 0.5
    }

    interface Speciation<T extends Comparable<T>> {
        int maximumSize();

        float weightDifferenceCoefficient(); // 1.0

        float disjointCoefficient(); // 2.0

        float excessCoefficient(); // 2.0

        float compatibilityThreshold(int generationNumber); // 6.0 ( * compatibilityThresholdModifier ^ generationNumber )

        float calculateCompatibility(GenomeDefault<T> genome1, GenomeDefault<T> genome2);

        default boolean belongs(final GenomeDefault<T> genome1, final GenomeDefault<T> genome2, final int generationNumber) {
            float compatibility = calculateCompatibility(genome1, genome2);

            return Float.compare(compatibility, compatibilityThreshold(generationNumber)) < 0;
        }

        float survivalThreshold(); // 0.2

        float elitistThreshold(); // 0.01

        int dropOffAge(); // 15
    }
}
