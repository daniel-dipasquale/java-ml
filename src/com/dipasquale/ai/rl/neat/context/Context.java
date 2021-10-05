package com.dipasquale.ai.rl.neat.context;

import com.dipasquale.ai.common.output.OutputClassifier;
import com.dipasquale.ai.common.sequence.OrderedGroup;
import com.dipasquale.ai.rl.neat.common.WeightMutationType;
import com.dipasquale.ai.rl.neat.core.NeatEnvironment;
import com.dipasquale.ai.rl.neat.genotype.Genome;
import com.dipasquale.ai.rl.neat.genotype.InnovationId;
import com.dipasquale.ai.rl.neat.genotype.NodeGene;
import com.dipasquale.ai.rl.neat.phenotype.GenomeActivator;
import com.dipasquale.ai.rl.neat.speciation.core.PopulationState;
import com.dipasquale.ai.rl.neat.speciation.core.ReproductionType;
import com.dipasquale.ai.rl.neat.speciation.core.Species;
import com.dipasquale.ai.rl.neat.speciation.metric.IterationMetrics;
import com.dipasquale.ai.rl.neat.speciation.organism.Organism;
import com.dipasquale.ai.rl.neat.speciation.strategy.fitness.SpeciesFitnessStrategy;
import com.dipasquale.ai.rl.neat.speciation.strategy.reproduction.SpeciesReproductionStrategy;
import com.dipasquale.ai.rl.neat.speciation.strategy.selection.SpeciesSelectionStrategyExecutor;
import com.dipasquale.common.Pair;
import com.dipasquale.synchronization.event.loop.IterableEventLoop;
import com.dipasquale.synchronization.wait.handle.WaitHandle;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
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

    MetricSupport metrics();

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
        boolean isEnabled();

        int numberOfThreads();
    }

    interface ParallelismSupport {
        ParallelismParameters params();

        <T> WaitHandle forEach(Iterator<T> iterator, Consumer<T> itemHandler);

        <T> WaitHandle forEach(List<T> list, Consumer<T> itemHandler);
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

        default <T> T generateItem(final OrderedGroup<? extends Comparable<?>, T> items) {
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
            float first = (float) index1;
            float total = (float) (size - 1);

            int index2 = isLessThan(first / total)
                    ? generateIndex(index1)
                    : generateIndex(index1 + 1, size);

            return new Pair<>(items.get(index1), items.get(index2));
        }

        <T> T generateItem(OutputClassifier<T> outputClassifier);
    }

    interface NodeGeneSupport {
        NodeGene createHidden();

        void setupInitialNodes(Genome genome);

        void reset();
    }

    interface ConnectionGeneSupport {
        float generateWeight();

        float perturbWeight(float weight);

        boolean shouldAllowRecurrent();

        boolean shouldAllowMultiCycle();

        void setupInitialConnections(Genome genome);

        InnovationId getOrCreateInnovationId(NodeGene inputNode, NodeGene outputNode);

        boolean containsInnovationId(InnovationId innovationId);

        void registerNodes(Genome genome);

        void deregisterNodes(Genome genome);

        void reset();
    }

    interface ActivationSupport {
        GenomeActivator getOrCreateActivator(Genome genome, PopulationState populationState);

        GenomeActivator createTransientActivator(Genome genome, PopulationState populationState);

        float calculateFitness(GenomeActivator genomeActivator);
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

        double compatibilityThreshold(int generation);

        float eugenicsThreshold();

        default int fitToReproduce(final int size) {
            int count1 = (int) Math.floor((double) eugenicsThreshold() * (double) size);
            int countFixed = Math.max(count1, 1);

            return Math.min(countFixed, size);
        }

        float elitistThreshold();

        int elitistThresholdMinimum();

        default int elitesToPreserve(final int size, final boolean includeRepresentative) {
            int count1 = (int) Math.floor((double) elitistThreshold() * (double) size);
            int countFixed = Math.max(count1, elitistThresholdMinimum());

            if (includeRepresentative) {
                return Math.min(countFixed, size);
            }

            return Math.min(countFixed, size - 1);
        }

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

        double calculateCompatibility(Genome genome1, Genome genome2);

        ReproductionType generateReproductionType(int organisms);

        SpeciesFitnessStrategy getFitnessStrategy();

        SpeciesSelectionStrategyExecutor getSelectionStrategy();

        SpeciesReproductionStrategy getReproductionStrategy();

        void disposeGenomeId(Genome genome);

        int getDisposedGenomeIdCount();
    }

    interface MetricSupport {
        void collectCompositions(Iterable<Species> allSpecies);

        void collectFitness(Species species, Organism organism);

        void collectFitness(Species species);

        void prepareNextFitnessCalculation();

        void prepareNextGeneration();

        void prepareNextIteration();

        Map<Integer, IterationMetrics> getMetrics();
    }

    interface StateOverrideSupport {
        NeatEnvironment fitnessFunction();

        IterableEventLoop eventLoop();
    }
}