package com.dipasquale.ai.rl.neat.context;

import com.dipasquale.ai.rl.neat.factory.WeightPerturber;
import com.dipasquale.ai.rl.neat.genotype.DefaultGenome;
import com.dipasquale.ai.rl.neat.genotype.GenomeGenesisConnector;
import com.dipasquale.ai.rl.neat.speciation.core.PopulationHistoricalMarkings;
import com.dipasquale.common.factory.FloatFactory;
import com.dipasquale.common.switcher.ObjectSwitcher;
import com.dipasquale.data.structure.map.SerializableInteroperableStateMap;
import com.dipasquale.threading.event.loop.EventLoopIterable;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public final class DefaultConnectionGeneSupportContext implements Context.ConnectionGeneSupport {
    private boolean multipleRecurrentCyclesAllowed;
    private ObjectSwitcher<FloatFactory> weightFactory;
    private ObjectSwitcher<WeightPerturber> weightPerturber;
    private GenomeGenesisConnector genomeGenesisConnector;

    @Override
    public boolean multipleRecurrentCyclesAllowed() {
        return multipleRecurrentCyclesAllowed;
    }

    @Override
    public float nextWeight() {
        return weightFactory.getObject().create();
    }

    @Override
    public float perturbWeight(final float weight) {
        return weightPerturber.getObject().perturb(weight);
    }

    @Override
    public void setupInitialConnections(final DefaultGenome genome, final PopulationHistoricalMarkings historicalMarkings) {
        genomeGenesisConnector.setupConnections(genome, historicalMarkings);
    }

    public void save(final SerializableInteroperableStateMap state) {
        state.put("connections.multipleRecurrentCyclesAllowed", multipleRecurrentCyclesAllowed);
        state.put("connections.weightFactory", weightFactory);
        state.put("connections.weightPerturber", weightPerturber);
        state.put("connections.genomeGenesisConnector", genomeGenesisConnector);
    }

    public void load(final SerializableInteroperableStateMap state, final EventLoopIterable eventLoop) {
        multipleRecurrentCyclesAllowed = state.get("connections.multipleRecurrentCyclesAllowed");
        weightFactory = ObjectSwitcher.switchObject(state.get("connections.weightFactory"), eventLoop != null);
        weightPerturber = ObjectSwitcher.switchObject(state.get("connections.weightPerturber"), eventLoop != null);
        genomeGenesisConnector = state.get("connections.genomeGenesisConnector");
    }
}
