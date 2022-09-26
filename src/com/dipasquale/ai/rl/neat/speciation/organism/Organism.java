package com.dipasquale.ai.rl.neat.speciation.organism;

import com.dipasquale.ai.rl.neat.NeatContext;
import com.dipasquale.ai.rl.neat.genotype.Genome;
import com.dipasquale.ai.rl.neat.genotype.NodeGeneType;
import com.dipasquale.ai.rl.neat.phenotype.GenomeActivator;
import com.dipasquale.ai.rl.neat.speciation.PopulationState;
import com.dipasquale.ai.rl.neat.speciation.Species;
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

    public float calculateCompatibility(final NeatContext.SpeciationSupport speciationSupport, final Species species) {
        return speciationSupport.calculateCompatibility(genome, species.getRepresentative().genome);
    }

    public int getHiddenNodeGenes() {
        return genome.getNodeGenes().size(NodeGeneType.HIDDEN);
    }

    public int getConnectionGenes() {
        return genome.getConnectionGenes().getExpressed().size();
    }

    public GenomeActivator provideActivator(final NeatContext.ActivationSupport activationSupport) {
        return activationSupport.provideActivator(genome, populationState, NeatContext.GenomeActivatorType.PERSISTENT);
    }

    public GenomeActivator createTransientActivator(final NeatContext.ActivationSupport activationSupport) {
        return activationSupport.provideActivator(genome, populationState, NeatContext.GenomeActivatorType.TRANSIENT);
    }

    public void setFitness(final Species species, final NeatContext context, final float fitnessValue) {
        fitness = fitnessValue;
        context.getMetrics().collectFitness(species, this);
    }

    public float updateFitness(final Species species, final NeatContext context) {
        NeatContext.ActivationSupport activationSupport = context.getActivation();
        GenomeActivator genomeActivator = provideActivator(activationSupport);
        float fitness = activationSupport.evaluateFitness(genomeActivator);

        setFitness(species, context, fitness);

        return fitness;
    }

    @Override
    public int compareTo(final Organism other) {
        return Float.compare(fitness, other.fitness);
    }

    public boolean mutate(final NeatContext context) {
        return genome.mutate(context);
    }

    public Organism mate(final NeatContext context, final Organism other) {
        int comparison = compareTo(other);
        NeatContext.CrossOverSupport crossOverSupport = context.getCrossOver();

        Genome crossedOverGenome = comparison == 0 ? crossOverSupport.crossOverByEqualTreatment(context, genome, other.genome)
                : comparison > 0 ? crossOverSupport.crossOverBySkippingUnfitDisjointOrExcess(context, genome, other.genome)
                : crossOverSupport.crossOverBySkippingUnfitDisjointOrExcess(context, other.genome, genome);

        return new Organism(crossedOverGenome, populationState);
    }

    public void registerNodeGenes(final NeatContext.NodeGeneSupport nodeGeneSupport) {
        nodeGeneSupport.registerAll(genome);
    }

    public Organism createCopy(final NeatContext context) {
        Genome copiedGenome = genome.createCopy(context);

        return new Organism(copiedGenome, populationState);
    }

    public Organism createClone(final NeatContext.ConnectionGeneSupport connectionGeneSupport) {
        Genome clonedGenome = genome.createClone(connectionGeneSupport);
        Organism clonedOrganism = new Organism(clonedGenome, populationState);

        clonedOrganism.fitness = fitness;

        return clonedOrganism;
    }

    public void kill(final NeatContext.SpeciationSupport speciationSupport) {
        speciationSupport.disposeGenomeId(genome);
    }

    public void deregisterNodeGenes(final NeatContext.NodeGeneSupport nodeGeneSupport) {
        nodeGeneSupport.deregisterAll(genome);
    }
}