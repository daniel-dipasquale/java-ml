package com.dipasquale.ai.rl.neat;

import com.dipasquale.ai.common.factory.WeightPerturber;
import com.dipasquale.ai.rl.neat.genotype.DirectedEdge;
import com.dipasquale.ai.rl.neat.genotype.GenesisGenomeConnector;
import com.dipasquale.ai.rl.neat.genotype.Genome;
import com.dipasquale.ai.rl.neat.genotype.HistoricalMarkings;
import com.dipasquale.ai.rl.neat.genotype.InnovationId;
import com.dipasquale.ai.rl.neat.genotype.NodeGene;
import com.dipasquale.common.factory.FloatFactory;
import com.dipasquale.common.gate.IsLessThanRandomGate;
import com.dipasquale.io.serialization.SerializableStateGroup;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
final class ContextObjectConnectionGeneSupport implements Context.ConnectionGeneSupport {
    private final FloatFactory weightFactory;
    private final RecurrentModifiersFactory recurrentWeightsFactory;
    private final WeightPerturber weightPerturber;
    private final IsLessThanRandomGate shouldAllowRecurrentGate;
    private final IsLessThanRandomGate shouldAllowUnrestrictedDirectionGate;
    private final IsLessThanRandomGate shouldAllowMultiCycleGate;
    private final GenesisGenomeConnector genesisGenomeConnector;
    private final HistoricalMarkings historicalMarkings;

    private static RecurrentModifiersFactory createRecurrentWeightsFactory(final InitializationContext initializationContext, final ConnectionGeneSettings connectionGeneSettings, final FloatFactory weightFactory) {
        float recurrentAllowanceRate = initializationContext.provideSingleton(connectionGeneSettings.getRecurrentAllowanceRate());

        if (Float.compare(recurrentAllowanceRate, 0f) <= 0) {
            return NoopRecurrentModifiersFactory.getInstance();
        }

        return new ProxyRecurrentModifiersFactory(weightFactory, connectionGeneSettings.getRecurrentStateType());
    }

    private static WeightPerturber createWeightPerturber(final InitializationContext initializationContext, final FloatNumber weightPerturber) {
        FloatFactory floatFactory = weightPerturber.createFactory(initializationContext);

        return new WeightPerturber(floatFactory);
    }

    private static IsLessThanRandomGate createIsLessThanRandomGate(final InitializationContext initializationContext, final FloatNumber maximum) {
        return new IsLessThanRandomGate(initializationContext.createDefaultRandomSupport(), initializationContext.provideSingleton(maximum));
    }

    static ContextObjectConnectionGeneSupport create(final InitializationContext initializationContext, final GenesisGenomeTemplate genesisGenomeTemplate, final ConnectionGeneSettings connectionGeneSettings) {
        FloatFactory weightFactory = connectionGeneSettings.getWeightFactory().createFactory(initializationContext);
        RecurrentModifiersFactory recurrentWeightsFactory = createRecurrentWeightsFactory(initializationContext, connectionGeneSettings, weightFactory);
        WeightPerturber weightPerturber = createWeightPerturber(initializationContext, connectionGeneSettings.getWeightPerturber());
        IsLessThanRandomGate shouldAllowRecurrentGate = createIsLessThanRandomGate(initializationContext, connectionGeneSettings.getRecurrentAllowanceRate());
        IsLessThanRandomGate shouldAllowUnrestrictedDirectionGate = createIsLessThanRandomGate(initializationContext, connectionGeneSettings.getUnrestrictedDirectionAllowanceRate());
        IsLessThanRandomGate shouldAllowMultiCycleGate = createIsLessThanRandomGate(initializationContext, connectionGeneSettings.getMultiCycleAllowanceRate());
        GenesisGenomeConnector genesisGenomeConnector = genesisGenomeTemplate.createConnector(initializationContext, weightFactory);
        HistoricalMarkings historicalMarkings = initializationContext.getHistoricalMarkings();

        return new ContextObjectConnectionGeneSupport(weightFactory, recurrentWeightsFactory, weightPerturber, shouldAllowRecurrentGate, shouldAllowUnrestrictedDirectionGate, shouldAllowMultiCycleGate, genesisGenomeConnector, historicalMarkings);
    }

    @Override
    public float generateWeight() {
        return weightFactory.create();
    }

    @Override
    public List<Float> generateRecurrentWeights() {
        return recurrentWeightsFactory.create();
    }

    @Override
    public List<Float> cloneRecurrentWeights(final List<Float> recurrentWeights) {
        return recurrentWeightsFactory.clone(recurrentWeights);
    }

    @Override
    public List<Float> createAverageRecurrentWeights(final List<Float> recurrentWeights1, final List<Float> recurrentWeights2) {
        return recurrentWeightsFactory.createAverage(recurrentWeights1, recurrentWeights2);
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
    public void reset() {
        historicalMarkings.clear();
    }

    void save(final SerializableStateGroup stateGroup) {
        stateGroup.put("connections.weightFactory", weightFactory);
        stateGroup.put("connections.recurrentWeightsFactory", recurrentWeightsFactory);
        stateGroup.put("connections.weightPerturber", weightPerturber);
        stateGroup.put("connections.shouldAllowRecurrentGate", shouldAllowRecurrentGate);
        stateGroup.put("connections.shouldAllowUnrestrictedDirectionGate", shouldAllowUnrestrictedDirectionGate);
        stateGroup.put("connections.shouldAllowMultiCycleGate", shouldAllowMultiCycleGate);
        stateGroup.put("connections.genesisGenomeConnector", genesisGenomeConnector);
        stateGroup.put("connections.historicalMarkings", historicalMarkings);
    }

    static ContextObjectConnectionGeneSupport create(final SerializableStateGroup stateGroup) {
        FloatFactory weightFactory = stateGroup.get("connections.weightFactory");
        RecurrentModifiersFactory recurrentWeightsFactory = stateGroup.get("connections.recurrentWeightsFactory");
        WeightPerturber weightPerturber = stateGroup.get("connections.weightPerturber");
        IsLessThanRandomGate shouldAllowRecurrentGate = stateGroup.get("connections.shouldAllowRecurrentGate");
        IsLessThanRandomGate shouldAllowUnrestrictedDirectionGate = stateGroup.get("connections.shouldAllowUnrestrictedDirectionGate");
        IsLessThanRandomGate shouldAllowMultiCycleGate = stateGroup.get("connections.shouldAllowMultiCycleGate");
        GenesisGenomeConnector genesisGenomeConnector = stateGroup.get("connections.genesisGenomeConnector");
        HistoricalMarkings historicalMarkings = stateGroup.get("connections.historicalMarkings");

        return new ContextObjectConnectionGeneSupport(weightFactory, recurrentWeightsFactory, weightPerturber, shouldAllowRecurrentGate, shouldAllowUnrestrictedDirectionGate, shouldAllowMultiCycleGate, genesisGenomeConnector, historicalMarkings);
    }
}
