package com.dipasquale.ai.rl.neat.speciation.organism;

import com.dipasquale.ai.rl.neat.context.Context;
import com.dipasquale.ai.rl.neat.genotype.Genome;
import com.dipasquale.ai.rl.neat.genotype.GenomeActivator;
import com.dipasquale.ai.rl.neat.speciation.core.PopulationState;
import com.dipasquale.ai.rl.neat.speciation.core.Species;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@RequiredArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public final class Organism implements Comparable<Organism>, Serializable {
    @Serial
    private static final long serialVersionUID = 752524381932687603L;
    @EqualsAndHashCode.Include
    private final Genome genome;
    private final PopulationState populationState;
    @Getter
    private float fitness = 0f;

    public double calculateCompatibility(final Context.SpeciationSupport speciationSupport, final Species species) {
        return speciationSupport.calculateCompatibility(genome, species.getRepresentative().genome);
    }

    public int getComplexity() {
        return genome.getComplexity();
    }

    public GenomeActivator getGenomeActivator(final Context.NeuralNetworkSupport neuralNetworkSupport) {
        return genome.getActivator(neuralNetworkSupport, populationState);
    }

    public float updateFitness(final Context.NeuralNetworkSupport neuralNetworkSupport) {
        GenomeActivator genomeActivator = getGenomeActivator(neuralNetworkSupport);

        return fitness = neuralNetworkSupport.calculateFitness(genomeActivator);
    }

    public void mutate(final Context context) {
        genome.mutate(context);
    }

    @Override
    public int compareTo(final Organism other) {
        return Float.compare(fitness, other.fitness);
    }

    public Organism mate(final Context context, final Organism other) {
        int comparison = compareTo(other);

        Genome crossedOverGenome = switch (comparison) {
            case 1 -> context.crossOver().crossOverBySkippingUnfitDisjointOrExcess(context, genome, other.genome);

            case 0 -> context.crossOver().crossOverByEqualTreatment(context, genome, other.genome);

            default -> context.crossOver().crossOverBySkippingUnfitDisjointOrExcess(context, other.genome, genome);
        };

        return new Organism(crossedOverGenome, populationState);
    }

    public Organism createCopy(final Context.SpeciationSupport speciationSupport) {
        Genome copiedGenome = genome.createCopy(speciationSupport);

        return new Organism(copiedGenome, populationState);
    }

    public Organism createClone() {
        Genome clonedGenome = genome.createClone();

        return new Organism(clonedGenome, populationState);
    }

    public void kill(final Context.SpeciationSupport speciationSupport) {
        speciationSupport.markToKill(genome);
    }
}