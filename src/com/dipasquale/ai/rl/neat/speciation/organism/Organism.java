package com.dipasquale.ai.rl.neat.speciation.organism;

import com.dipasquale.ai.rl.neat.context.Context;
import com.dipasquale.ai.rl.neat.genotype.DefaultGenome;
import com.dipasquale.ai.rl.neat.phenotype.NeuralNetwork;
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
    private final DefaultGenome genome;
    private final PopulationState populationState;
    @Getter
    private float fitness = 0f;

    public double calculateCompatibility(final Context.SpeciationSupport speciationSupport, final Species species) {
        return speciationSupport.calculateCompatibility(genome, species.getRepresentative().genome);
    }

    public int getComplexity() {
        return genome.getComplexity();
    }

    public float updateFitness(final Context.NeuralNetworkSupport neuralNetworkSupport) {
        return fitness = genome.calculateFitness(neuralNetworkSupport, populationState);
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

        DefaultGenome crossedOverGenome = switch (comparison) {
            case 1 -> context.crossOver().crossOverBySkippingUnfitDisjointOrExcess(context, genome, other.genome);

            case 0 -> context.crossOver().crossOverByEqualTreatment(context, genome, other.genome);

            default -> context.crossOver().crossOverBySkippingUnfitDisjointOrExcess(context, other.genome, genome);
        };

        return new Organism(crossedOverGenome, populationState);
    }

    public NeuralNetwork getPhenotype(final Context.NeuralNetworkSupport neuralNetworkSupport) {
        return neuralNetworkSupport.getPhenotype(genome);
    }

    public Organism createCopy(final Context.SpeciationSupport speciationSupport) {
        DefaultGenome copiedGenome = genome.createCopy(speciationSupport);

        return new Organism(copiedGenome, populationState);
    }

    public Organism createClone() {
        DefaultGenome clonedGenome = genome.createClone();

        return new Organism(clonedGenome, populationState);
    }

    public void kill(final Context.SpeciationSupport speciationSupport) {
        speciationSupport.markToKill(genome);
    }
}