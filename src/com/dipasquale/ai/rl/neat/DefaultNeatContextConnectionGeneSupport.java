package com.dipasquale.ai.rl.neat;

import com.dipasquale.ai.rl.neat.factory.RecurrentWeightFactory;
import com.dipasquale.ai.rl.neat.factory.WeightPerturber;
import com.dipasquale.ai.rl.neat.generational.gate.GenerationalIsLessThanRandomGate;
import com.dipasquale.ai.rl.neat.genotype.DirectedEdge;
import com.dipasquale.ai.rl.neat.genotype.GenesisGenomeConnector;
import com.dipasquale.ai.rl.neat.genotype.Genome;
import com.dipasquale.ai.rl.neat.genotype.HistoricalMarkings;
import com.dipasquale.ai.rl.neat.genotype.InnovationId;
import com.dipasquale.ai.rl.neat.genotype.NodeGene;
import com.dipasquale.common.factory.FloatFactory;
import com.dipasquale.io.serialization.SerializableStateGroup;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class DefaultNeatContextConnectionGeneSupport implements NeatContext.ConnectionGeneSupport {
    private final FloatFactory weightFactory;
    private final RecurrentWeightFactory recurrentWeightFactory;
    private final WeightPerturber weightPerturber;
    private final GenerationalIsLessThanRandomGate shouldAllowRecurrentGate;
    private final GenerationalIsLessThanRandomGate shouldAllowUnrestrictedDirectionGate;
    private final GenerationalIsLessThanRandomGate shouldAllowMultiCycleGate;
    private final GenesisGenomeConnector genesisGenomeConnector;
    private final HistoricalMarkings historicalMarkings;

    @Override
    public float generateWeight() {
        return weightFactory.create();
    }

    @Override
    public List<Float> generateRecurrentWeights() {
        return recurrentWeightFactory.create();
    }

    @Override
    public List<Float> cloneRecurrentWeights(final List<Float> recurrentWeights) {
        return recurrentWeightFactory.clone(recurrentWeights);
    }

    @Override
    public List<Float> createAverageRecurrentWeights(final List<Float> recurrentWeights1, final List<Float> recurrentWeights2) {
        return recurrentWeightFactory.createAverage(recurrentWeights1, recurrentWeights2);
    }

    @Override
    public float perturbWeight(final float weight) {
        return weightPerturber.perturb(weight);
    }

    @Override
    public boolean shouldAllowRecurrent() {
        return shouldAllowRecurrentGate.isOn();
    }

    @Override
    public boolean shouldAllowUnrestrictedDirection() {
        return shouldAllowUnrestrictedDirectionGate.isOn();
    }

    @Override
    public boolean shouldAllowMultiCycle() {
        return shouldAllowMultiCycleGate.isOn();
    }

    @Override
    public void setupInitial(final Genome genome) {
        genesisGenomeConnector.setupConnections(genome, this);
    }

    @Override
    public InnovationId provideInnovationId(final NodeGene sourceNodeGene, final NodeGene targetNodeGene) {
        return historicalMarkings.provideInnovationId(new DirectedEdge(sourceNodeGene, targetNodeGene));
    }

    @Override
    public boolean containsInnovationId(final InnovationId innovationId) {
        return historicalMarkings.containsInnovationId(innovationId.getDirectedEdge());
    }

    @Override
    public void advanceGeneration() {
        shouldAllowRecurrentGate.reinitialize();
        shouldAllowUnrestrictedDirectionGate.reinitialize();
        shouldAllowMultiCycleGate.reinitialize();
    }

    void save(final SerializableStateGroup stateGroup) {
        stateGroup.put("connections.weightFactory", weightFactory);
        stateGroup.put("connections.recurrentWeightFactory", recurrentWeightFactory);
        stateGroup.put("connections.weightPerturber", weightPerturber);
        stateGroup.put("connections.shouldAllowRecurrentGate", shouldAllowRecurrentGate);
        stateGroup.put("connections.shouldAllowUnrestrictedDirectionGate", shouldAllowUnrestrictedDirectionGate);
        stateGroup.put("connections.shouldAllowMultiCycleGate", shouldAllowMultiCycleGate);
        stateGroup.put("connections.genesisGenomeConnector", genesisGenomeConnector);
        stateGroup.put("connections.historicalMarkings", historicalMarkings);
    }

    static DefaultNeatContextConnectionGeneSupport create(final SerializableStateGroup stateGroup) {
        FloatFactory weightFactory = stateGroup.get("connections.weightFactory");
        RecurrentWeightFactory recurrentWeightFactory = stateGroup.get("connections.recurrentWeightFactory");
        WeightPerturber weightPerturber = stateGroup.get("connections.weightPerturber");
        GenerationalIsLessThanRandomGate shouldAllowRecurrentGate = stateGroup.get("connections.shouldAllowRecurrentGate");
        GenerationalIsLessThanRandomGate shouldAllowUnrestrictedDirectionGate = stateGroup.get("connections.shouldAllowUnrestrictedDirectionGate");
        GenerationalIsLessThanRandomGate shouldAllowMultiCycleGate = stateGroup.get("connections.shouldAllowMultiCycleGate");
        GenesisGenomeConnector genesisGenomeConnector = stateGroup.get("connections.genesisGenomeConnector");
        HistoricalMarkings historicalMarkings = stateGroup.get("connections.historicalMarkings");

        return new DefaultNeatContextConnectionGeneSupport(weightFactory, recurrentWeightFactory, weightPerturber, shouldAllowRecurrentGate, shouldAllowUnrestrictedDirectionGate, shouldAllowMultiCycleGate, genesisGenomeConnector, historicalMarkings);
    }
}
