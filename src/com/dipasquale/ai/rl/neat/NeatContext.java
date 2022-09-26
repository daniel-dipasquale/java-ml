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
import com.dipasquale.ai.rl.neat.speciation.strategy.fitness.FitnessEvaluationStrategy;
import com.dipasquale.ai.rl.neat.speciation.strategy.reproduction.ReproductionStrategy;
import com.dipasquale.ai.rl.neat.speciation.strategy.selection.SelectionStrategyExecutor;
import com.dipasquale.common.FloatValue;
import com.dipasquale.common.Pair;
import com.dipasquale.common.random.ProbabilityClassifier;
import com.dipasquale.data.structure.group.ListSetGroup;
import com.dipasquale.synchronization.event.loop.ParallelEventLoop;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

public interface NeatContext {
    ParallelismSupport getParallelism();

    RandomnessSupport getRandomness();

    ActivationSupport getActivation();

    NodeGeneSupport getNodeGenes();

    ConnectionGeneSupport getConnectionGenes();

    MutationSupport getMutation();

    CrossOverSupport getCrossOver();

    SpeciationSupport getSpeciation();

    MetricsSupport getMetrics();

    void save(ObjectOutputStream outputStream) throws IOException;

    interface ParallelismParameters {
        boolean isEnabled();

        int getNumberOfThreads();
    }

    interface ParallelismSupport {
        ParallelismParameters getParams();

        FloatValue createFloatValue(float initialValue);

        <T> void forEach(Iterator<T> iterator, Consumer<T> elementHandler);

        <T> void forEach(List<T> list, Consumer<T> elementHandler);
    }

    interface RandomnessSupport {
        int generateIndex(int offset, int count);

        default int generateIndex(final int count) {
            return generateIndex(0, count);
        }

        default <T> T generateElement(final List<T> elements) {
            int size = elements.size();

            if (size == 0) {
                return null;
            }

            int index = generateIndex(size);

            return elements.get(index);
        }

        default <T> T generateElement(final ListSetGroup<? extends Comparable<?>, T> elements) {
            int size = elements.size();

            if (size == 0) {
                return null;
            }

            int index = generateIndex(size);

            return elements.getByIndex(index);
        }

        boolean isLessThan(float rate);

        default <T> T generateElement(final T element1, final T element2) {
            if (isLessThan(0.5f)) {
                return element1;
            }

            return element2;
        }

        default <T> Pair<T> generateElementPair(final List<T> elements) {
            int size = elements.size();

            if (size <= 1) {
                return null;
            }

            if (size == 2) {
                return new Pair<>(elements.get(0), elements.get(1));
            }

            int index1 = generateIndex(size);
            int index2 = generateIndex(size - 1);

            if (index2 < index1) {
                return new Pair<>(elements.get(index1), elements.get(index2));
            }

            return new Pair<>(elements.get(index1), elements.get(index2 + 1));
        }

        <T> T generateElement(ProbabilityClassifier<T> probabilityClassifier);

        <T> void shuffle(List<T> elements);
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    enum GenomeActivatorType {
        PERSISTENT,
        TRANSIENT
    }

    interface ActivationSupport {
        void initialize(int populationSize);

        GenomeActivator provideActivator(Genome genome, PopulationState populationState, GenomeActivatorType type);

        float evaluateFitness(GenomeActivator genomeActivator);

        List<Float> evaluateAllFitness(NeatContext context, List<GenomeActivator> genomeActivators);

        void advanceGeneration(int populationSize);

        void clear();
    }

    interface NodeGeneSupport {
        NodeGene createHidden();

        void setupInitial(Genome genome);

        void registerAll(Genome genome);

        void deregisterAll(Genome genome);

        void clear();
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

        void setupInitial(Genome genome);

        InnovationId provideInnovationId(NodeGene sourceNodeGene, NodeGene targetNodeGene);

        boolean containsInnovationId(InnovationId innovationId);

        void advanceGeneration();
    }

    interface MutationSupport {
        boolean shouldAddNode();

        boolean shouldAddConnection();

        WeightMutationType generateWeightMutationType();

        boolean shouldDisableExpressedConnection();

        void advanceGeneration();
    }

    interface CrossOverSupport {
        boolean shouldOverrideExpressedConnection();

        boolean shouldUseWeightFromRandomParent();

        default Genome crossOverBySkippingUnfitDisjointOrExcess(final NeatContext context, final Genome fitParent, final Genome unfitParent) {
            return Genome.crossOverBySkippingUnfitDisjointOrExcess(context, fitParent, unfitParent);
        }

        default Genome crossOverByEqualTreatment(final NeatContext context, final Genome parent1, final Genome parent2) {
            return Genome.crossOverByEqualTreatment(context, parent1, parent2);
        }

        void advanceGeneration();
    }

    interface SpeciationSupport {
        int getPopulationSize();

        int getMaximumSpecies();

        String createSpeciesId();

        int createGenomeId();

        Genome createGenesisGenome(NeatContext context);

        float calculateCompatibilityThreshold(int generation);

        float calculateCompatibility(Genome genome1, Genome genome2);

        float getEugenicsThreshold();

        default int calculateFitToReproduce(final int size) {
            int count1 = (int) Math.floor((double) getEugenicsThreshold() * (double) size);
            int count = Math.max(count1, 1);

            return Math.min(count, size);
        }

        float getElitistThreshold();

        int getMinimumElitistDesired();

        default int determineElitesToPreserve(final int size, final boolean includeRepresentative) {
            int count1 = (int) Math.floor((double) getElitistThreshold() * (double) size);
            int count = Math.max(count1, getMinimumElitistDesired());

            if (includeRepresentative) {
                return Math.min(count, size);
            }

            return Math.min(count, size - 1);
        }

        ReproductionType generateReproductionType(int organisms);

        int getStagnationDropOffAge();

        float getInterSpeciesMatingRate();

        FitnessEvaluationStrategy getFitnessEvaluationStrategy();

        SelectionStrategyExecutor getSelectionStrategy();

        ReproductionStrategy getReproductionStrategy();

        void disposeGenomeId(Genome genome);

        int getDisposedGenomeIdCount();

        int advanceGeneration(int populationSize);

        void clear();
    }

    @FunctionalInterface
    interface MetricsParameters {
        boolean isEnabled();
    }

    interface MetricsSupport {
        MetricsParameters params();

        void collectAllSpeciesCompositions(Iterable<Species> allSpecies);

        void collectFitness(Species species, Organism organism);

        void collectFitness(Species species);

        void collectKilled(Species species, List<Organism> organismsKilled);

        void collectExtinction(Species species, boolean extinct);

        void prepareNextFitnessEvaluation();

        void prepareNextGeneration();

        void prepareNextIteration();

        MetricsViewer createMetricsViewer();
    }

    interface PretrainedSupport {
        NeatEnvironment getFitnessFunction();

        ParallelEventLoop getEventLoop();
    }
}