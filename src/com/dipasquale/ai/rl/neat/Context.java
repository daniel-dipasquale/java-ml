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
        float addNodeMutationsRate();

        float addConnectionMutationsRate();

        float perturbConnectionWeightRate();

        float replaceConnectionWeightRate();

        float disableConnectionExpressedRate();
    }

    interface CrossOver {
        float rate();

        float overrideExpressedRate();

        float useRandomParentWeightRate();

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

        float eugenicsThreshold(); // 0.2

        float elitistThreshold(); // 0.01

        int elitistThresholdMinimum(); // 1

        int stagnationDropOffAge(); // 15

        float interSpeciesMatingRate(); // 0.01
    }
}
