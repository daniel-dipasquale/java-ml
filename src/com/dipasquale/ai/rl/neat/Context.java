package com.dipasquale.ai.rl.neat;

import com.dipasquale.ai.rl.neat.genotype.Genome;
import com.dipasquale.ai.rl.neat.genotype.InnovationId;
import com.dipasquale.ai.rl.neat.genotype.NodeGene;
import com.dipasquale.ai.rl.neat.phenotype.GenomeActivator;
import com.dipasquale.ai.rl.neat.speciation.PopulationState;
import com.dipasquale.ai.rl.neat.speciation.ReproductionType;
import com.dipasquale.ai.rl.neat.speciation.Species;
import com.dipasquale.ai.rl.neat.speciation.metric.MetricsViewer;
import com.dipasquale.ai.rl.neat.speciation.organism.Organism;
import com.dipasquale.ai.rl.neat.speciation.strategy.fitness.FitnessCalculationStrategy;
import com.dipasquale.ai.rl.neat.speciation.strategy.reproduction.ReproductionStrategy;
import com.dipasquale.ai.rl.neat.speciation.strategy.selection.SelectionStrategyExecutor;
import com.dipasquale.common.Pair;
import com.dipasquale.common.random.ProbabilityClassifier;
import com.dipasquale.data.structure.group.ListSetGroup;
import com.dipasquale.synchronization.event.loop.ParallelEventLoop;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

public interface Context {
    GeneralSupport general();

    ParallelismSupport parallelism();

    RandomSupport random();

    NodeGeneSupport nodes();

    ConnectionGeneSupport connections();

    ActivationSupport activation();

    MutationSupport mutation();

    CrossOverSupport crossOver();

    SpeciationSupport speciation();

    MetricsSupport metrics();

    void save(ObjectOutputStream outputStream) throws IOException;

    void load(ObjectInputStream inputStream, StateOverrideSupport override) throws IOException, ClassNotFoundException;

    @FunctionalInterface
    interface GeneralParams {
        int populationSize();
    }

    @FunctionalInterface
    interface GeneralSupport {
        GeneralParams params();
    }

    interface ParallelismParameters {
        boolean enabled();

        int numberOfThreads();
    }

    interface ParallelismSupport {
        ParallelismParameters params();

        <T> void forEach(Iterator<T> iterator, Consumer<T> itemHandler);

        <T> void forEach(List<T> list, Consumer<T> itemHandler);
    }

    interface RandomSupport {
        int generateIndex(int offset, int count);

        default int generateIndex(final int count) {
            return generateIndex(0, count);
        }

        default <T> T generateItem(final List<T> items) {
            int size = items.size();

            if (size == 0) {
                return null;
            }

            int index = generateIndex(size);

            return items.get(index);
        }

        default <T> T generateItem(final ListSetGroup<? extends Comparable<?>, T> items) {
            int size = items.size();

            if (size == 0) {
                return null;
            }

            int index = generateIndex(size);

            return items.getByIndex(index);
        }

        boolean isLessThan(float rate);

        default <T> Pair<T> generateItemPair(final List<T> items) {
            int size = items.size();

            if (size <= 1) {
                return null;
            }

            if (size == 2) {
                return new Pair<>(items.get(0), items.get(1));
            }

            int index1 = generateIndex(size);
            int index2 = generateIndex(size - 1);

            if (index2 < index1) {
                return new Pair<>(items.get(index1), items.get(index2));
            }

            return new Pair<>(items.get(index1), items.get(index2 + 1));
        }

        <T> T generateItem(ProbabilityClassifier<T> probabilityClassifier);

        <T> void shuffle(List<T> items);
    }

    interface NodeGeneSupport {
        NodeGene createHidden();

        void setupInitialNodes(Genome genome);

        void reset();
    }

    interface ConnectionGeneSupport {
        float generateWeight();

        List<Float> generateRecurrentWeights();

        List<Float> cloneRecurrentWeights(List<Float> recurrentWeights);

        List<Float> createAverageRecurrentWeights(List<Float> recurrentWeights1, List<Float> recurrentWeights2);

        float perturbWeight(float weight);

        boolean shouldAllowRecurrent();

        boolean shouldAllowUnrestrictedDirection();

        boolean shouldAllowMultiCycle();

        void setupInitialConnections(Genome genome);

        InnovationId provideInnovationId(NodeGene sourceNode, NodeGene targetNode);

        boolean containsInnovationId(InnovationId innovationId);

        void registerNodes(Genome genome);

        void deregisterNodes(Genome genome);

        void reset();
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    enum GenomeActivatorType {
        PERSISTENT,
        TRANSIENT
    }

    interface ActivationSupport {
        GenomeActivator provideActivator(Genome genome, PopulationState populationState, GenomeActivatorType type);

        float calculateFitness(GenomeActivator genomeActivator);

        List<Float> calculateAllFitness(Context context, List<GenomeActivator> genomeActivators);
    }

    interface MutationSupport {
        boolean shouldAddNode();

        boolean shouldAddConnection();

        WeightMutationType generateWeightMutationType();

        boolean shouldDisableExpressedConnection();
    }

    interface CrossOverSupport {
        boolean shouldOverrideExpressedConnection();

        boolean shouldUseWeightFromRandomParent();

        default Genome crossOverBySkippingUnfitDisjointOrExcess(final Context context, final Genome fitParent, final Genome unfitParent) {
            return Genome.crossOverBySkippingUnfitDisjointOrExcess(context, fitParent, unfitParent);
        }

        default Genome crossOverByEqualTreatment(final Context context, final Genome parent1, final Genome parent2) {
            return Genome.crossOverByEqualTreatment(context, parent1, parent2);
        }
    }

    interface SpeciationParameters {
        int maximumSpecies();

        float compatibilityThreshold(int generation);

        float eugenicsThreshold();

        float elitistThreshold();

        int elitistThresholdMinimum();

        int stagnationDropOffAge();

        float interSpeciesMatingRate();
    }

    interface SpeciationSupport {
        SpeciationParameters params();

        String createSpeciesId();

        void clearSpeciesIds();

        String createGenomeId();

        void clearGenomeIds();

        Genome createGenesisGenome(Context context);

        float calculateCompatibility(Genome genome1, Genome genome2);

        default int calculateFitToReproduce(final int size) {
            int count1 = (int) Math.floor((double) params().eugenicsThreshold() * (double) size);
            int count = Math.max(count1, 1);

            return Math.min(count, size);
        }

        default int determineElitesToPreserve(final int size, final boolean includeRepresentative) {
            int count1 = (int) Math.floor((double) params().elitistThreshold() * (double) size);
            int count = Math.max(count1, params().elitistThresholdMinimum());

            if (includeRepresentative) {
                return Math.min(count, size);
            }

            return Math.min(count, size - 1);
        }

        ReproductionType generateReproductionType(int organisms);

        FitnessCalculationStrategy getFitnessCalculationStrategy();

        SelectionStrategyExecutor getSelectionStrategy();

        ReproductionStrategy getReproductionStrategy();

        void disposeGenomeId(Genome genome);

        int getDisposedGenomeIdCount();
    }

    @FunctionalInterface
    interface MetricsParameters {
        boolean enabled();
    }

    interface MetricsSupport {
        MetricsParameters params();

        void collectInitialCompositions(Iterable<Species> allSpecies);

        void collectFitness(Species species, Organism organism);

        void collectFitness(Species species);

        void collectKilled(Species species, List<Organism> organismsKilled);

        void collectExtinction(Species species, boolean extinct);

        void prepareNextFitnessCalculation();

        void prepareNextGeneration();

        void prepareNextIteration();

        MetricsViewer createMetricsViewer();
    }

    interface StateOverrideSupport {
        NeatEnvironment fitnessFunction();

        ParallelEventLoop eventLoop();
    }
}