package com.dipasquale.ai.rl.neat.speciation.organism;

import com.dipasquale.ai.rl.neat.context.Context;
import com.dipasquale.ai.rl.neat.genotype.Genome;
import com.dipasquale.ai.rl.neat.genotype.NodeGeneType;
import com.dipasquale.ai.rl.neat.phenotype.GenomeActivator;
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
    @EqualsAndHashCode.Include
    private final PopulationState populationState;
    @Getter
    private float fitness = 0f;

    public double calculateCompatibility(final Context.SpeciationSupport speciationSupport, final Species species) {
        return speciationSupport.calculateCompatibility(genome, species.getRepresentative().genome);
    }

    public int getHiddenNodes() {
        return genome.getNodes().size(NodeGeneType.HIDDEN);
    }

    public int getConnections() {
        return genome.getConnections().getExpressed().size();
    }

    public GenomeActivator getGenomeActivator(final Context.ActivationSupport activationSupport) {
        return activationSupport.getOrCreateGenomeActivator(genome, populationState);
    }

    public float updateFitness(final Species species, final Context context) { // TODO: should I let an exception bubble up from here, if the fitness functions fail
        Context.ActivationSupport activationSupport = context.activation();
        GenomeActivator genomeActivator = getGenomeActivator(activationSupport);

        fitness = activationSupport.calculateFitness(genomeActivator);
        context.metrics().addFitness(species, this);

        return fitness;
    }

    @Override
    public int compareTo(final Organism other) {
        return Float.compare(fitness, other.fitness);
    }

    public boolean mutate(final Context context) {
        return genome.mutate(context);
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

    public void registerNodes(final Context.ConnectionGeneSupport connectionGeneSupport) {
        connectionGeneSupport.registerNodes(genome);
    }

    public Organism createCopy(final Context context) {
        Genome genomeCopied = genome.createCopy(context);

        return new Organism(genomeCopied, populationState);
    }

    public Organism createClone(final Context.ConnectionGeneSupport connectionGeneSupport) {
        Genome genomeCloned = genome.createClone(connectionGeneSupport);
        Organism organismCloned = new Organism(genomeCloned, populationState);

        organismCloned.fitness = fitness;

        return organismCloned;
    }

    public void kill(final Context.SpeciationSupport speciationSupport) {
        speciationSupport.disposeGenomeId(genome);
    }

    public void deregisterNodes(final Context.ConnectionGeneSupport connectionGeneSupport) {
        connectionGeneSupport.deregisterNodes(genome);
    }
}